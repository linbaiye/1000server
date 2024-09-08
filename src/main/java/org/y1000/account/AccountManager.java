package org.y1000.account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.factory.PlayerFactory;
import org.y1000.persistence.PlayerPo;
import org.y1000.repository.AccountRepository;
import org.y1000.repository.PlayerRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public final class AccountManager {

    private final EntityManagerFactory entityManagerFactory;

    private final AccountRepository accountRepository;

    private final Supplier<byte[]> saltSupplier;

    private final PlayerRepository playerRepository;

    private final PlayerFactory playerFactory;

    private final Map<String, Integer> loginTokens;

    private static final String KEY = "2e087019fe5b9582735cf732c72dfde7";

    public AccountManager(EntityManagerFactory entityManagerFactory,
                          AccountRepository accountRepository, PlayerRepository playerRepository, PlayerFactory playerFactory) {
        this(entityManagerFactory, accountRepository, AccountManager::randSalt, playerRepository, playerFactory);
    }


    public AccountManager(EntityManagerFactory entityManagerFactory,
                          AccountRepository accountRepository,
                          Supplier<byte[]> saltSupplier,
                          PlayerRepository playerRepository,
                          PlayerFactory playerFactory) {
        Validate.notNull(saltSupplier);
        Validate.notNull(entityManagerFactory);
        Validate.notNull(accountRepository);
        Validate.notNull(playerFactory);
        Validate.notNull(playerRepository);
        this.playerRepository = playerRepository;
        this.playerFactory = playerFactory;
        this.saltSupplier = saltSupplier;
        this.accountRepository = accountRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.loginTokens = new ConcurrentHashMap<>();
    }

    private static byte[] randSalt() {
        byte[] salt = new byte[16];
        ThreadLocalRandom.current().nextBytes(salt);
        return salt;
    }

    private byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 10000, 256);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    private boolean isExpectedPassword(char[] password, byte[] salt, byte[] expectedHash) {
        byte[] pwdHash = hash(password, salt);
        Arrays.fill(password, Character.MIN_VALUE);
        if (pwdHash.length != expectedHash.length) return false;
        for (int i = 0; i < pwdHash.length; i++) {
            if (pwdHash[i] != expectedHash[i]) return false;
        }
        return true;
    }

    private boolean isExpectedPassword(String password, String salt, String expectedHash) {
        Base64.Decoder decoder = Base64.getDecoder();
        return isExpectedPassword(password.toCharArray(), decoder.decode(salt), decoder.decode(expectedHash));
    }


    private <R> R doTransaction(Function<EntityManager, R> action) {
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            var r = action.apply(entityManager);
            transaction.commit();
            return r;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw new RuntimeException(e);
        }
    }

        // http status code.
    public RegisterResponse register(String username, String passwd) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            if (accountRepository.find(entityManager, username).isPresent()) {
                return new RegisterResponse(1, "账号已存在");
            }
            var salt = saltSupplier.get();
            byte[] hashedPasswd = hash(passwd.toCharArray(), salt);
            Base64.Encoder encoder = Base64.getEncoder();
            Account account = Account.builder()
                    .salt(new String(encoder.encode(salt)))
                    .hashedPassword(new String(encoder.encode(hashedPasswd)))
                    .userName(username)
                    .createdTime(LocalDateTime.now())
                    .build();
            accountRepository.save(entityManager, account);
            transaction.commit();
        }
        return new RegisterResponse(0, null);
    }


    private String cacheLoginToken(String username, int accountId) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            var tmp = username + KEY + new String(encoder.encode(randSalt()));
            final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            final byte[] hashbytes = digest.digest(tmp.getBytes(StandardCharsets.UTF_8));
            String token = new String(encoder.encode(hashbytes));
            loginTokens.put(token, accountId);
            return token;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public LoginResponse login(String username, String passwd) {
        if (username == null || passwd == null) {
            return LoginResponse.badRequest();
        }
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Optional<Account> accountOptional = accountRepository.find(entityManager, username);
            if (accountOptional.isEmpty()) {
                return LoginResponse.badCredentials();
            }
            Account account = accountOptional.get();
            if (!isExpectedPassword(passwd, account.getSalt(), account.getHashedPassword())) {
                return LoginResponse.badCredentials();
            }
            String token = cacheLoginToken(username, account.getId());
            return LoginResponse.ok(account.getPlayers().stream().map(PlayerPo::getName).collect(Collectors.toList()), token);
        } catch (Exception e) {
            return LoginResponse.serverError();
        }
    }

    public CreateCharResponse createCharacter(String token, String charName, boolean male) {
        if (token == null || StringUtils.isBlank(charName)) {
            return CreateCharResponse.badRequest("请输入角色名");
        }
        Integer accountId = loginTokens.get(token);
        if (accountId == null) {
            return CreateCharResponse.badRequest("请登录");
        }
        if (charName.length() > 5) {
            return CreateCharResponse.badRequest("角色名过长（5字符）");
        }
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            int count = playerRepository.countByName(entityManager, charName);
            if (count > 0) {
                return CreateCharResponse.badRequest("角色已存在");
            }
            count = playerRepository.countByAccount(entityManager, accountId);
            if (count > 5) {
                return CreateCharResponse.badRequest("最多创建5个角色");
            }
            Player player = playerFactory.create(charName, male);
            playerRepository.save(entityManager, accountId, player);
            transaction.commit();
            return CreateCharResponse.ok(charName);
        } catch (Exception e) {
            log.error("Failed to handle request, ", e);
            if (transaction != null)
                transaction.rollback();
        }
        return CreateCharResponse.serverError();
    }

    public Optional<Integer> removeToken(String token) {
        return Optional.ofNullable(loginTokens.remove(token));
    }
}

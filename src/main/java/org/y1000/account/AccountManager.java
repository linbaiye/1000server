package org.y1000.account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.factory.PlayerFactory;
import org.y1000.message.account.AccountMessage;
import org.y1000.message.account.LoginRequest;
import org.y1000.network.Connection;
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
import java.util.function.Supplier;

@Slf4j
public final class AccountManager {

    private final EntityManagerFactory entityManagerFactory;

    private final AccountRepository accountRepository;

    private final Supplier<byte[]> saltSupplier;

    private final PlayerRepository playerRepository;

    private final PlayerFactory playerFactory;

    public AccountManager(EntityManagerFactory entityManagerFactory,
                          AccountRepository accountRepository, PlayerRepository playerRepository, PlayerFactory playerFactory) {
        this(entityManagerFactory, accountRepository, AccountManager::randSalt, playerRepository, playerFactory);
    }


    private record CharacterNameId(String name, long id){ }

    private final Map<Connection, LoginInfo> loggedConnections;

    private record LoginInfo(int accountId, List<CharacterNameId> characterNameIds) {

        public int characterNumber() {
            return characterNameIds.size();
        }

        public boolean containsName(String name) {
            return characterNameIds.stream().anyMatch(n -> n.name.equals(name));
        }
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
        this.loggedConnections = new HashMap<>();
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

    private boolean isPasswordOk(char[] password, byte[] salt, byte[] expectedHash) {
        byte[] pwdHash = hash(password, salt);
        Arrays.fill(password, Character.MIN_VALUE);
        if (pwdHash.length != expectedHash.length) return false;
        for (int i = 0; i < pwdHash.length; i++) {
            if (pwdHash[i] != expectedHash[i]) return false;
        }
        return true;
    }

    private boolean isPasswordOk(String password, String salt, String expectedHash) {
        Base64.Decoder decoder = Base64.getDecoder();
        return isPasswordOk(password.toCharArray(), decoder.decode(salt), decoder.decode(expectedHash));
    }


    public void register(Connection connection, String username, String passwd) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            if (accountRepository.find(entityManager, username).isPresent()) {
                connection.writeAndFlush(new RegisterResponse(1, "账号已存在"));
                transaction.rollback();
                return;
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
        connection.writeAndFlush(new RegisterResponse(0, "注册成功，请返回登陆页面登陆账号"));
    }


    public void handle(Connection connection, AccountMessage message) {
        if (message instanceof LoginRequest request) {
            handleLogin(connection, request.name(), request.password());
        }
    }

    private void handleLogin(Connection connection, String username, String passwd) {
        if (username == null || passwd == null) {
            connection.writeAndFlush(LoginResponse.badRequest());
            return;
        }
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Optional<Account> accountOptional = accountRepository.find(entityManager, username);
            if (accountOptional.isEmpty()) {
                connection.writeAndFlush(LoginResponse.badCredentials());
                return;
            }
            Account account = accountOptional.get();
            if (!isPasswordOk(passwd, account.getSalt(), account.getHashedPassword())) {
                connection.writeAndFlush(LoginResponse.badCredentials());
            }
            List<PlayerPo> players = account.getPlayers();
            List<CharacterNameId> characterNameIds = new ArrayList<>();
            List<String> ret = new ArrayList<>();
            for (PlayerPo player : players) {
                characterNameIds.add(new CharacterNameId(player.getName(), player.getId()));
                ret.add(player.getName());
            }
            loggedConnections.put(connection, new LoginInfo(account.getId(), characterNameIds));
            connection.writeAndFlush(LoginResponse.ok(ret));
        } catch (Exception e) {
            connection.writeAndFlush(LoginResponse.serverError());
        }
    }

    private static boolean isValidName(String str) {
        if (StringUtils.isEmpty(str) || str.length() > 8) {
            return false;
        }
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (!Character.isIdeographic(c)) {
                return false;
            }
        }
        return true;
    }

    public void createCharacter(Connection connection, String charName, boolean male) {
        if (!loggedConnections.containsKey(connection)) {
            connection.writeAndFlush(CreateCharResponse.badRequest("请登录"));
            return;
        }
        if (StringUtils.isBlank(charName)) {
            connection.writeAndFlush(CreateCharResponse.badRequest("请输入角色名"));
            return;
        }
        if (!isValidName(charName)) {
            connection.writeAndFlush(CreateCharResponse.badRequest("角色名只能是汉字且不可超8字符"));
            return;
        }
        LoginInfo loginInfo = loggedConnections.get(connection);
        if (loginInfo.characterNumber() >= 5) {
            connection.writeAndFlush(CreateCharResponse.badRequest("最多创建5个角色"));
            return;
        }
        if (loginInfo.containsName(charName)) {
            connection.writeAndFlush(CreateCharResponse.badRequest("角色已存在"));
            return;
        }
        Player player = playerFactory.create(charName, male);

        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            playerRepository.save(entityManager, loginInfo.accountId, player);
            transaction.commit();
            connection.writeAndFlush(CreateCharResponse.ok(charName));
        } catch (Exception e) {
            log.error("Failed to handle request, ", e);
            if (transaction != null)
                transaction.rollback();
        }
        connection.writeAndFlush(CreateCharResponse.serverError());
    }
}

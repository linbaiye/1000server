package org.y1000.account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerFactory;
import org.y1000.network.Connection;
import org.y1000.persistence.PlayerPo;
import org.y1000.repository.AccountRepository;
import org.y1000.repository.PlayerRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Slf4j
public final class AccountManagerImpl implements AccountManager {

    private final EntityManagerFactory entityManagerFactory;

    private final AccountRepository accountRepository;

    private final Supplier<byte[]> saltSupplier;

    private final PlayerRepository playerRepository;

    private final PlayerFactory playerFactory;

    public AccountManagerImpl(EntityManagerFactory entityManagerFactory,
                              AccountRepository accountRepository, PlayerRepository playerRepository, PlayerFactory playerFactory) {
        this(entityManagerFactory, accountRepository, AccountManagerImpl::randSalt, playerRepository, playerFactory);
    }


    private record NameIdRealm(String name, long id, long realmId){ }

    private final Map<Connection, LoginInfo> loggedConnections;

    private record LoginInfo(int accountId, List<NameIdRealm> nameIdRealms) {

        public int characterNumber() {
            return nameIdRealms.size();
        }
        public boolean containsName(String name) {
            return nameIdRealms.stream().anyMatch(n -> n.name.equals(name));
        }
    }


    public AccountManagerImpl(EntityManagerFactory entityManagerFactory,
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


    private void register(Connection connection, String username, String passwd) {
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


    @Override
    public void handle(Connection connection, AccountMessage message) {
        if (message instanceof LoginAccountRequest request) {
            handleLogin(connection, request.name(), request.password());
        } else if (message instanceof RegisterAccountRequest r) {
            register(connection, r.username(), r.password());
        } else if (message instanceof CreateCharacterRequest characterRequest) {
            createCharacter(connection, characterRequest.name(), characterRequest.male());
        }
    }

    @Override
    public List<Long> getAllPlayerId(Connection connection) {
        LoginInfo loginInfo = loggedConnections.get(connection);
        return loginInfo != null ? loginInfo.nameIdRealms.stream().map(NameIdRealm::id).toList() :
                Collections.emptyList();
    }

    @Override
    public long[] loginCharacter(Connection connection, String charName) {
        LoginInfo loginInfo = loggedConnections.remove(connection);
        if (loginInfo == null)
            return null;
        for (NameIdRealm nameIdRealm : loginInfo.nameIdRealms()) {
            if (nameIdRealm.name.equals(charName)) {
                return new long[]{nameIdRealm.id(), nameIdRealm.realmId()};
            }
        }
        return null;
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
            List<NameIdRealm> nameIdRealms = new ArrayList<>();
            List<String> ret = new ArrayList<>();
            for (PlayerPo player : players) {
                nameIdRealms.add(new NameIdRealm(player.getName(), player.getId(), player.getRealmId()));
                ret.add(player.getName());
            }
            loggedConnections.put(connection, new LoginInfo(account.getId(), nameIdRealms));
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

    private void createCharacter(Connection connection, String charName, boolean male) {
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

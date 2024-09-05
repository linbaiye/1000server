package org.y1000.account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.Validate;
import org.y1000.repository.AccountRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public final class AccountManager {

    private final EntityManagerFactory entityManagerFactory;

    private final AccountRepository accountRepository;

    private final Supplier<byte[]> saltSupplier;

    public AccountManager(EntityManagerFactory entityManagerFactory,
                          AccountRepository accountRepository) {
        this(entityManagerFactory, accountRepository, AccountManager::randSalt);
    }

    private static byte[] randSalt() {
        byte[] salt = new byte[16];
        ThreadLocalRandom.current().nextBytes(salt);
        return salt;
    }

    public AccountManager(EntityManagerFactory entityManagerFactory,
                          AccountRepository accountRepository,
                          Supplier<byte[]> saltSupplier) {
        Validate.notNull(saltSupplier);
        Validate.notNull(entityManagerFactory);
        Validate.notNull(accountRepository);
        this.saltSupplier = saltSupplier;
        this.accountRepository = accountRepository;
        this.entityManagerFactory = entityManagerFactory;
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
        return isExpectedPassword(password.toCharArray(), salt.getBytes(), expectedHash.getBytes());
    }

        // http status code.
    public int register(String username, String passwd) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (accountRepository.find(entityManager, username).isPresent()) {
            return 409;
        }  
        var salt = saltSupplier.get();
        byte[] hashedPasswd = hash(passwd.toCharArray(), salt);
        Base64.Encoder encoder = Base64.getEncoder();
        Account account = Account.builder()
                .salt(new String(encoder.encode(salt)))
                .hashedPassword(new String(hashedPasswd))
                .userName(username)
                .build();
        accountRepository.save(entityManager, account);
        transaction.commit();
        return 200;
    }

    public LoginResponse login(String username, String passwd) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Account account = entityManager.createQuery("select a from Account a left join fetch a.players where a.userName = ?1",
                        Account.class)
                .setParameter(1, username).getSingleResult();
        if (account == null) {
            return new LoginResponse()
        }
    }
}

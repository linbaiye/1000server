package org.y1000.account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.persistence.PlayerPo;
import org.y1000.repository.AccountRepository;
import org.y1000.repository.AccountRepositoryImpl;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AccountManagerTest {

    private AccountManager accountManager;

    private AccountRepository accountRepository;

    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepositoryImpl();
        entityManagerFactory = Persistence.createEntityManagerFactory("org.y1000.test");
        accountManager = new AccountManager(entityManagerFactory, accountRepository, "salt"::getBytes);
    }

    @Test
    void registerWhenNotExist() {
        assertEquals(200, accountManager.register("user", "passwd"));
        Account account = accountRepository.find(entityManagerFactory.createEntityManager(), "user").get();
        assertEquals("user", account.getUserName());
        assertEquals("9VhBH2owxsk5Tv13Abdp64Kbu+I2ZC8RFtbdIeZOfAw=", account.getHashedPassword());
        assertEquals("c2FsdA==", account.getSalt());
    }

    @Test
    void registerWhenConflict() {
        accountManager.register("user", "passwd");
        assertEquals(409, accountManager.register("user", "passwd"));
    }

    @Test
    void loginWithNotExistingUser() {
        LoginResponse response = accountManager.login("user", "passwd");
        assertEquals(404, response.getStatus());
    }

    @Test
    void loginWhenNoChars() {
        accountManager.register("user", "passwd");
        LoginResponse response = accountManager.login("user", "passwd");
        assertEquals(200, response.getStatus());
        assertTrue(response.getCharNames().isEmpty());
    }

    @Test
    void loginWithChars() {
        accountManager.register("user", "passwd");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Account user = accountRepository.find(entityManager, "user").get();
        PlayerPo p = PlayerPo.builder().accountId(user.getId()).name("test").build();
        entityManager.persist(p);
        transaction.commit();
        LoginResponse response = accountManager.login("user", "passwd");
        assertEquals(200, response.getStatus());
        assertTrue(response.getCharNames().contains("test"));
    }
}
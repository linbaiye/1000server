package org.y1000.account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.factory.PlayerFactory;
import org.y1000.persistence.PlayerPo;
import org.y1000.repository.AccountRepository;
import org.y1000.repository.AccountRepositoryImpl;
import org.y1000.repository.PlayerRepository;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


class AccountManagerTest extends AbstractPlayerUnitTestFixture  {

    private AccountManager accountManager;

    private AccountRepository accountRepository;

    private EntityManagerFactory entityManagerFactory;

    private PlayerFactory playerFactory;
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepositoryImpl();
        entityManagerFactory = Persistence.createEntityManagerFactory("org.y1000.test");
        playerFactory = Mockito.mock(PlayerFactory.class);
        playerRepository = Mockito.mock(PlayerRepository.class);
        accountManager = new AccountManager(entityManagerFactory, accountRepository, "salt"::getBytes, playerRepository, playerFactory);
    }

    @Test
    void registerWhenNotExist() {
        assertEquals(0, accountManager.register("user", "passwd").getCode());
        Account account = accountRepository.find(entityManagerFactory.createEntityManager(), "user").get();
        assertEquals("user", account.getUserName());
        assertEquals("9VhBH2owxsk5Tv13Abdp64Kbu+I2ZC8RFtbdIeZOfAw=", account.getHashedPassword());
        assertEquals("c2FsdA==", account.getSalt());
    }

    @Test
    void registerWhenConflict() {
        accountManager.register("user", "passwd");
        assertEquals(1, accountManager.register("user", "passwd").getCode());
    }

    @Test
    void loginWithNotExistingUser() {
        LoginResponse response = accountManager.login("user", "passwd");
        assertEquals(1, response.getStatus());
    }

    @Test
    void loginWhenNoChars() {
        accountManager.register("user", "passwd");
        LoginResponse response = accountManager.login("user", "passwd");
        assertEquals(0, response.getStatus());
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
        assertEquals(0, response.getStatus());
        assertTrue(response.getCharNames().contains("test"));
    }

    @Test
    void createCharacter() {
        accountManager.register("user", "passwd");
        LoginResponse login = accountManager.login("user", "passwd");
        player = playerBuilder().build();
        when(playerFactory.create(anyString(), anyBoolean())).thenReturn(player);
        CreateCharResponse response = accountManager.createCharacter(login.getToken(), "cha😊", true);
        assertEquals(0, response.getCode());
    }

    @Test
    void createCharacterWhenExists() {
        accountManager.register("user", "passwd");
        LoginResponse login = accountManager.login("user", "passwd");
        when(playerRepository.countByName(any(EntityManager.class), anyString())).thenReturn(1);
        var ret = accountManager.createCharacter(login.getToken(), "cha", true);
        assertEquals(1, ret.getCode());
    }
}
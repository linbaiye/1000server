package org.y1000.repository;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.account.Account;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.item.ItemFactory;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryImplTest extends AbstractPlayerUnitTestFixture {

    private AccountRepositoryImpl accountRepository;

    private JpaFixture entityManager;

    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        setup();
        accountRepository = new AccountRepositoryImpl();
        entityManager = new JpaFixture();
        playerRepository = new PlayerRepositoryImpl(Mockito.mock(ItemFactory.class), createKungFuBookRepositoryImpl(), Mockito.mock(KungFuBookRepository.class), Mockito.mock(EntityManagerFactory.class), createItemRepository(), Mockito.mock(GuildRepository.class));
    }

    @Test
    void save() {
        Account account = Account.builder().userName("test").hashedPassword("h").salt("s").build();
        var em = entityManager.beginTx();
        accountRepository.save(em, account);
        entityManager.submitTx();
        assertNotNull(account.getId());
    }


    @Test
    void find() {
        Account account = Account.builder().userName("test").hashedPassword("h").salt("s").build();
        var em = entityManager.beginTx();
        accountRepository.save(em, account);
        playerRepository.save(em, account.getId(), player);
        entityManager.submitTx();
        Account test = accountRepository.find(entityManager.newEntityManager(), "test").get();
        System.out.println(test.getPlayers());
    }
}
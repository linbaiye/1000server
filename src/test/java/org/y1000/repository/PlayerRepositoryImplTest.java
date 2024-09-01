package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.entities.players.PlayerExperiencedAgedAttribute;
import org.y1000.entities.players.PlayerLife;
import org.y1000.entities.players.YinYang;
import org.y1000.item.ItemFactory;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuBookFactory;
import org.y1000.persistence.PlayerPo;
import org.y1000.util.Coordinate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


class PlayerRepositoryImplTest extends AbstractPlayerUnitTestFixture {

    private TestingEntityManager entityManager;
    private PlayerRepositoryImpl playerRepository;

    private ItemFactory itemFactory;
    private KungFuBookFactory kungFuBookFactory;
    private KungFuBookRepository kungFuBookRepository;

    @BeforeEach
    void setUp() {
        setup();
        itemFactory = Mockito.mock(ItemFactory.class);
        kungFuBookFactory = Mockito.mock(KungFuBookFactory.class);
        kungFuBookRepository = Mockito.mock(KungFuBookRepository.class);
        playerRepository = new PlayerRepositoryImpl(itemFactory, kungFuBookFactory, kungFuBookRepository);
        entityManager = new TestingEntityManager();
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
    }

    @Test
    void save() {
        player = playerBuilder().yinYang(new YinYang(100, 200))
                .id(0L)
                .life(new PlayerLife(0, 100, 10))
                .head(new PlayerLife(0, 100, 11))
                .arm(new PlayerLife(0, 100, 12))
                .leg(new PlayerLife(0, 100, 13))
                .male(false)
                .power(new PlayerExperiencedAgedAttribute(0, 1, 14, 100))
                .innerPower(new PlayerExperiencedAgedAttribute(0, 2, 15, 100))
                .outerPower(new PlayerExperiencedAgedAttribute(0, 3, 16, 100))
                .revival(17)
                .build();
        player.joinRealm(mockedRealm, Coordinate.xy(1, 2));
        var em = entityManager.beginTx();
        playerRepository.save(em, 1, player);
        entityManager.submitTx();
        List resultList = em.createQuery("select p from PlayerPo p").getResultList();
        var playerPo = (PlayerPo)resultList.get(0);
        //PlayerPo playerPo = em.find(PlayerPo.class, player.id());
        assertEquals(100, playerPo.getYin());
        assertEquals(200, playerPo.getYang());
        assertEquals(10, playerPo.getLife());
        assertEquals(11, playerPo.getHeadLife());
        assertEquals(12, playerPo.getArmLife());
        assertEquals(13, playerPo.getLegLife());
        assertEquals(14, playerPo.getPower());
        assertEquals(15, playerPo.getInnerPower());
        assertEquals(16, playerPo.getOuterPower());
        assertEquals(17, playerPo.getRevivalExp());
        assertFalse(playerPo.isMale());
        assertEquals(player.viewName(), playerPo.getName());
        assertEquals(player.coordinate(), playerPo.coordinate());
        assertEquals(player.getRealm().id(), playerPo.getRealmId());
        assertEquals(1, playerPo.getAccountId());
        doAnswer(invocationOnMock -> {
            assertEquals(playerPo.getId(), invocationOnMock.getArgument(1));
            return null;
        }).when(kungFuBookRepository).save(any(EntityManager.class), anyLong(), any(KungFuBook.class));
        verify(kungFuBookRepository, times(1)).save(any(EntityManager.class), anyLong(), any(KungFuBook.class));
    }

    @Test
    void update() {
        var em = entityManager.beginTx();
        playerRepository.save(em, 1, player);
        entityManager.submitTx();
        List resultList = em.createQuery("select p from PlayerPo p").getResultList();
        var playerPo = (PlayerPo)resultList.get(0);
        player = playerBuilder().yinYang(new YinYang(3000, 4000))
                .id(playerPo.getId())
                .life(new PlayerLife(0, 100, 10))
                .head(new PlayerLife(0, 100, 11))
                .arm(new PlayerLife(0, 100, 12))
                .leg(new PlayerLife(0, 100, 13))
                .build();
        player.joinRealm(mockAllFlatRealm(), Coordinate.xy(1, 3));
        em = entityManager.beginTx();
        playerRepository.update(em, player);
        entityManager.submitTx();
        resultList = em.createQuery("select p from PlayerPo p").getResultList();
        playerPo = (PlayerPo)resultList.get(0);
        assertEquals(3000, playerPo.yinYang().yinExp());
        assertEquals(4000, playerPo.yinYang().yangExp());
    }
}
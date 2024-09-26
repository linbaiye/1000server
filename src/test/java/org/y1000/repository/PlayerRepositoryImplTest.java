package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.ItemFactory;
import org.y1000.item.Weapon;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuBookFactory;
import org.y1000.persistence.EquipmentPo;
import org.y1000.persistence.PlayerPo;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


class PlayerRepositoryImplTest extends AbstractPlayerUnitTestFixture {

    private JpaFixture jpaFixture;
    private PlayerRepositoryImpl playerRepository;

    private ItemFactory itemFactory;

    private KungFuBookFactory kungFuBookFactory;

    private KungFuBookRepository kungFuBookRepository;

    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        setup();
        itemFactory = createItemFactory();
        kungFuBookFactory = Mockito.mock(KungFuBookFactory.class);
        kungFuBookRepository = Mockito.mock(KungFuBookRepository.class);
        jpaFixture = new JpaFixture();
        itemRepository = Mockito.mock(ItemRepository.class);
        GuildRepository guildRepository = Mockito.mock(GuildRepository.class);
        playerRepository = new PlayerRepositoryImpl(itemFactory, kungFuBookFactory, kungFuBookRepository, jpaFixture.getEntityManagerFactory(), itemRepository, guildRepository);
    }

    @AfterEach
    void tearDown() {
        jpaFixture.close();
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
        var em = jpaFixture.beginTx();
        playerRepository.save(em, 1, player);
        jpaFixture.submitTx();
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
        assertNotEquals(0, playerPo.getRealmId());
        assertEquals(1, playerPo.getAccountId());
        doAnswer(invocationOnMock -> {
            assertEquals(playerPo.getId(), invocationOnMock.getArgument(1));
            return null;
        }).when(kungFuBookRepository).save(any(EntityManager.class), anyLong(), any(KungFuBook.class));
        verify(kungFuBookRepository, times(1)).save(any(EntityManager.class), anyLong(), any(KungFuBook.class));
    }

    @Test
    void update() {
        var em = jpaFixture.beginTx();
        long id = playerRepository.save(em, 1, player);
        jpaFixture.submitTx();
        player = playerBuilder().yinYang(new YinYang(3000, 4000))
                .id(id)
                .life(new PlayerLife(0, 100, 10))
                .head(new PlayerLife(0, 100, 11))
                .arm(new PlayerLife(0, 100, 12))
                .leg(new PlayerLife(0, 100, 13))
                .male(false)
                .hair(itemFactory.createHair("女子长发"))
                .trouser(itemFactory.createTrouser("女子长裤"))
                .boot(itemFactory.createBoot("女子皮鞋"))
                .hat(itemFactory.createHat("女子斗笠"))
                .chest(itemFactory.createChest("女子黄龙弓服"))
                .wrist(itemFactory.createWrist("女子黄龙手套"))
                .clothing(itemFactory.createClothing("女子上衣"))
                .weapon((Weapon) itemFactory.createEquipment("长剑"))
                .build();
        player.joinRealm(mockAllFlatRealm(), Coordinate.xy(1, 3));
        em = jpaFixture.beginTx();
        playerRepository.update(player);
        jpaFixture.submitTx();
        var resultList = em.createQuery("select p from PlayerPo p").getResultList();
        var playerPo = (PlayerPo)resultList.get(0);
        assertEquals(3000, playerPo.yinYang().yinExp());
        assertEquals(4000, playerPo.yinYang().yangExp());
        List<String> names = em.createQuery("select e from EquipmentPo e", EquipmentPo.class).getResultStream().map(EquipmentPo::getName).toList();
        assertTrue(names.contains("女子长发"));
        assertTrue(names.contains("女子长裤"));
        assertTrue(names.contains("女子皮鞋"));
        assertTrue(names.contains("女子斗笠"));
        assertTrue(names.contains("女子黄龙弓服"));
        assertTrue(names.contains("女子黄龙手套"));
        assertTrue(names.contains("女子上衣"));
        assertTrue(names.contains("长剑"));
        verify(itemRepository, times(1)).save(any(EntityManager.class), any(Player.class));
    }

    @Test
    void count() {
        var em = jpaFixture.newEntityManager();
        int count = playerRepository.countByName(em, player.viewName());
        assertEquals(0, count);
        em = jpaFixture.beginTx();
        playerRepository.save(em, 1, player);
        jpaFixture.submitTx();
        em = jpaFixture.newEntityManager();
        count = playerRepository.countByName(em, player.viewName());
        assertEquals(1, count);
    }


    private void assertPlayerLifeEquals(PlayerLife life, PlayerLife life2) {
        assertEquals(life.maxValue(), life2.maxValue());
        assertEquals(life.currentValue(), life2.currentValue());
    }

    private void assertAttributeEquals(PlayerExperiencedAgedAttribute attr1, PlayerExperiencedAgedAttribute attr2) {
        assertEquals(attr1.maxValue(), attr2.maxValue());
        assertEquals(attr1.currentValue(), attr2.currentValue());
        assertEquals(attr1.exp(), attr2.exp());
    }

    @Test
    void find() {
        PlayerDefaultAttributes innate = PlayerDefaultAttributes.INSTANCE;
        YinYang yinYang = new YinYang(100, 200);
        var em = jpaFixture.beginTx();
        long id = playerRepository.save(em, 1, player);
        jpaFixture.submitTx();
        player = playerBuilder().yinYang(yinYang)
                .id(id)
                .life(new PlayerLife(innate.life(), yinYang.age(), 10))
                .head(new PlayerLife(innate.life(), yinYang.age(), 11))
                .arm(new PlayerLife(innate.life(), yinYang.age(), 12))
                .leg(new PlayerLife(innate.life(), yinYang.age(), 13))
                .male(false)
                .power(new PlayerExperiencedAgedAttribute(innate.power(), 1, 14, yinYang.age()))
                .innerPower(new PlayerExperiencedAgedAttribute(innate.innerPower(), 2, 15, yinYang.age()))
                .outerPower(new PlayerExperiencedAgedAttribute(innate.outerPower(), 3, 16, yinYang.age()))
                .revival(17)
                .hair(itemFactory.createHair("女子长发"))
                .trouser(itemFactory.createTrouser("女子长裤"))
                .boot(itemFactory.createBoot("女子皮鞋"))
                .hat(itemFactory.createHat("女子斗笠"))
                .chest(itemFactory.createChest("女子黄龙弓服"))
                .wrist(itemFactory.createWrist("女子黄龙手套"))
                .clothing(itemFactory.createClothing("女子上衣"))
                .weapon((Weapon) itemFactory.createEquipment("长剑"))
                .build();
        player.joinRealm(mockedRealm, Coordinate.xy(1, 2));
        playerRepository.update(player);
        when(itemRepository.findInventory(any(EntityManager.class), anyLong())).thenReturn(new Inventory());
        KungFuBook kungFuBook = createKungFuBookFactory().create();
        when(kungFuBookRepository.find(any(EntityManager.class), anyLong())).thenReturn(Optional.of(kungFuBook));
        var p = playerRepository.find(1, player.viewName()).get().getLeft();
        assertEquals(player.viewName(), p.viewName());
        assertPlayerLifeEquals(player.legLife(), p.legLife());
        assertPlayerLifeEquals(player.headLife(), p.headLife());
        assertPlayerLifeEquals(player.armLife(), p.armLife());
        assertAttributeEquals(player.powerAttribute(), p.powerAttribute());
        assertAttributeEquals(player.innerPowerAttribute(), p.innerPowerAttribute());
        assertAttributeEquals(player.outerPowerAttribute(), p.outerPowerAttribute());
        assertEquals(player.isMale(), p.isMale());
        assertEquals(player.revivalExp(), p.revivalExp());
        assertEquals("长剑", p.weapon().get().name());
        assertEquals("女子黄龙弓服", p.chest().get().name());
        assertEquals("女子黄龙手套", p.wrist().get().name());
        assertEquals("女子斗笠", p.hat().get().name());
        assertEquals("女子长发", p.hair().get().name());
        assertEquals("女子长裤", p.trouser().get().name());
        assertEquals("女子皮鞋", p.boot().get().name());
        assertEquals("女子上衣", p.clothing().get().name());
        assertEquals("无名剑法", p.attackKungFu().name());
        verify(itemRepository, times(1)).findInventory(any(EntityManager.class), anyLong());
    }
}
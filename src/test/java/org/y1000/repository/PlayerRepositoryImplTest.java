package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.*;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuBookFactory;
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
        itemRepository = createItemRepository();
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
                .hair(itemFactory.createHair("女子长发"))
                .revival(17)
                .build();
        player.joinRealm(mockedRealm, Coordinate.xy(1, 2));
        var em = jpaFixture.beginTx();
        playerRepository.save(em, 1, player);
        jpaFixture.submitTx();
        List resultList = em.createQuery("select p from PlayerPo p").getResultList();
        var playerPo = (PlayerPo)resultList.get(0);
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
        assertEquals(1, playerPo.getEquipments().size());
        assertNotNull(player.hair().get().id());
        doAnswer(invocationOnMock -> {
            assertEquals(playerPo.getId(), invocationOnMock.getArgument(1));
            return null;
        }).when(kungFuBookRepository).save(any(EntityManager.class), anyLong(), any(KungFuBook.class));
        verify(kungFuBookRepository, times(1)).save(any(EntityManager.class), anyLong(), any(KungFuBook.class));
    }

    @Test
    void update() {
        Inventory inventory = new Inventory();
        Item dye = itemFactory.createItem("天蓝染剂", 1);
        var em = jpaFixture.beginTx();
        SexualEquipment hair = itemFactory.createHair("女子长发");
        ArmorEquipment boot = itemFactory.createBoot("女子皮鞋");
        hair.findAbility(Dyable.class).get().dye(dye.color());
        Weapon w1 = (Weapon) itemFactory.createEquipment("新罗宝剑");
        player = playerBuilder().male(true).id(0L).name("123").hair(hair).boot(boot).weapon(w1).build();
        long id = playerRepository.save(em, 1, player);
        jpaFixture.submitTx();
        hair.findAbility(Dyable.class).get().dye(dye.color() + 1);
        Weapon w2 = (Weapon) itemFactory.createEquipment("新罗宝剑");
        player = playerBuilder().yinYang(new YinYang(3000, 4000))
                .id(id)
                .life(new PlayerLife(0, 100, 10))
                .head(new PlayerLife(0, 100, 11))
                .arm(new PlayerLife(0, 100, 12))
                .leg(new PlayerLife(0, 100, 13))
                .hair(hair)
                .chest(itemFactory.createChest("男子黄龙弓服"))
                .weapon(w2)
                .build();
        player.joinRealm(mockAllFlatRealm(), Coordinate.xy(1, 3));
        playerRepository.update(player);
        KungFuBook kungFuBook = createKungFuBookFactory().create();
        when(kungFuBookRepository.find(any(EntityManager.class), anyLong())).thenReturn(Optional.of(kungFuBook));
        var updated = playerRepository.find(1, "123").get().getKey();
        assertEquals(w2.id(), updated.weapon().get().id());
        assertEquals(dye.color() + 1, updated.hair().get().color());
        assertFalse(updated.boot().isPresent());
        assertTrue(updated.chest().isPresent());
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
        player = playerBuilder().yinYang(yinYang)
                .id(0)
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
        playerRepository.save(em, 1, player);
        jpaFixture.submitTx();
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
    }
}
package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.objects.DynamicObjectFactoryImpl;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.persistence.GuildMembershipPo;
import org.y1000.persistence.GuildStonePo;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.util.Coordinate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GuildRepositoryImplTest {

    private GuildRepository guildRepository;
    private JpaFixture jpaFixture;

    private final DynamicObjectFactory dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
    private RealmMap realmMap;
    private long id;

    private Realm realm;

    @BeforeEach
    void setUp() {
        realmMap = Mockito.mock(RealmMap.class);
        realm = Mockito.mock(Realm.class);
        when(realm.map()).thenReturn(realmMap);
        when(realm.id()).thenReturn(1);
        id = 1;
        jpaFixture = new JpaFixture();
        guildRepository = new GuildRepositoryImpl(jpaFixture.getEntityManagerFactory());
    }


    private GuildStone createStone(Coordinate coordinate, String name) {
        return new GuildStone(id++, coordinate, 1000000, 1000000, name, null, 1);
    }

    private void saveStone(GuildStone stone, long creator) {
        EntityManager entityManager = jpaFixture.beginTx();
        guildRepository.save(entityManager, stone, creator);
        entityManager.getTransaction().commit();
    }


    @Test
    void findByRealm() {
        var stone1 = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(2, 3));
        saveStone(stone1, 1L);
        var stone2 = dynamicObjectFactory.createGuildStone(2, "test2", 1, realmMap, Coordinate.xy(4, 5));
        saveStone(stone2, 2L);
        List<GuildStone> guildStones = guildRepository.findByRealm(realm, new EntityIdGenerator());
        assertEquals(2, guildStones.size());
        var stone = guildStones.stream().filter(s -> s.guildName().equals("test")).findFirst().get();
        assertEquals(1, stone.getRealm().id());
        assertEquals(Coordinate.xy(2, 3), stone.coordinate());
        assertEquals(stone1.getMaxLife(), stone.getMaxLife());
        assertEquals(stone1.currentLife(), stone.currentLife());
    }

    @Test
    void countByName() {
        var stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(2, 3));
        saveStone(stone, 1L);
        assertEquals(1, guildRepository.countByName("test"));
    }

    @Test
    void save() {
        Player founder = Mockito.mock(Player.class);
        when(founder.getRealm()).thenReturn(realm);
        when(founder.id()).thenReturn(2L);
        var stone = createStone(Coordinate.xy(1, 1), "test");
        stone.foundedBy(founder);
        guildRepository.save(stone);
        var stonePo = jpaFixture.newEntityManager().createQuery("select p from GuildStonePo p where p.id = ?1", GuildStonePo.class)
                .setParameter(1, stone.getGuildId()).getResultList().get(0);
        assertNull(stonePo.getGuildKungFuPo());
        assertFalse(stonePo.getMembers().isEmpty());
        assertEquals(Coordinate.xy(1,1), stonePo.coordinate());
        assertEquals("test", stonePo.getName());
        assertEquals(realm.id(), stonePo.getRealmId());
    }

//    @Test
//    void findGuildMembership() {
//        var stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(2, 3));
//        saveStone(stone, 4L);
//        EntityManager entityManager = jpaFixture.beginTx();
//        guildRepository.upsertMembership(entityManager, 4L, new GuildMembership("test", "guild"));
//        jpaFixture.submitTx();
//        var guildMembership = guildRepository.findGuildMembership(jpaFixture.newEntityManager(), 4L);
//        assertTrue(guildMembership.isPresent());
//        assertEquals("test", guildMembership.get().guildName());
//    }

    @Test
    void update() {
        var stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(2, 3));
        saveStone(stone, 4L);
        Player player = Mockito.mock(Player.class);
        when(player.damage()).thenReturn(new Damage(1, 1,1,1));
        when(player.coordinate()).thenReturn(stone.coordinate().moveBy(Direction.RIGHT));
//        stone.attackedBy(player);
        guildRepository.update(stone);
        GuildStonePo guildStonePo = jpaFixture.newEntityManager().find(GuildStonePo.class, stone.getGuildId());
        assertEquals(stone.getMaxLife() - 1, guildStonePo.getCurrentHealth());
    }

//    @Test
//    void deleteGuildAndMembership() {
//        var stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(2, 3));
//        saveStone(stone, 4L);
//        EntityManager entityManager = jpaFixture.beginTx();
//        guildRepository.upsertMembership(entityManager, 4L, new GuildMembership(stone.getGuildId(), "test", "guild"));
//        guildRepository.deleteGuildAndMembership(stone.getGuildId());
//        assertNull(jpaFixture.newEntityManager().find(GuildStonePo.class, stone.getGuildId()));
//        assertNull(jpaFixture.newEntityManager().find(GuildMembershipPo.class, 4L));
//    }
}
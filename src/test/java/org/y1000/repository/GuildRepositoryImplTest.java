package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.objects.DynamicObjectFactoryImpl;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.persistence.GuildMembershipPo;
import org.y1000.persistence.GuildStonePo;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GuildRepositoryImplTest {

    private GuildRepository guildRepository;
    private JpaFixture jpaFixture;

    private final DynamicObjectFactory dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
    private RealmMap realmMap;

    @BeforeEach
    void setUp() {
        realmMap = Mockito.mock(RealmMap.class);
        jpaFixture = new JpaFixture();
        guildRepository = new GuildRepositoryImpl(jpaFixture.getEntityManagerFactory());
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
        List<GuildStone> guildStones = guildRepository.findByRealm(1, new EntityIdGenerator(), realmMap);
        assertEquals(2, guildStones.size());
        var stone = guildStones.stream().filter(s -> s.idName().equals("test")).findFirst().get();
        assertEquals(1, stone.getRealmId());
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
        var stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(2, 3));
        saveStone(stone, 4L);
        GuildStonePo guildStonePo = jpaFixture.newEntityManager().createQuery("select s from GuildStonePo s where s.name = ?1", GuildStonePo.class)
                .setParameter(1, "test").getResultList().get(0);
        assertEquals("test", guildStonePo.getName());
        assertEquals(1, guildStonePo.getRealmId());
        assertEquals(2, guildStonePo.getX());
        assertEquals(3, guildStonePo.getY());
    }

    @Test
    void findGuildMembership() {
        var stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(2, 3));
        saveStone(stone, 4L);
        EntityManager entityManager = jpaFixture.beginTx();
        guildRepository.update(entityManager, 4L, new GuildMembership(stone.getPersistentId(), "test", "guild"));
        jpaFixture.submitTx();
        var guildMembership = guildRepository.findGuildMembership(jpaFixture.newEntityManager(), 4L);
        assertTrue(guildMembership.isPresent());
        assertEquals("test", guildMembership.get().guildName());
    }
}
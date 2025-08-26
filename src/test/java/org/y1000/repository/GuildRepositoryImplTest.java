package org.y1000.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.Player;
import org.y1000.guild.Guild;
import org.y1000.input.ApplyGuildKungFuInput;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.persistence.GuildKungFuPo;
import org.y1000.persistence.GuildPo;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GuildRepositoryImplTest extends AbstractUnitTestFixture {

    private GuildRepository guildRepository;
    private JpaFixture jpaFixture;
    private RealmMap realmMap;
    private long id;
    private Realm realm;
    private Player founder;
    private Guild stone;
    private long playerId = 1;

    @BeforeEach
    void setUp() {
        realmMap = Mockito.mock(RealmMap.class);
        realm = Mockito.mock(Realm.class);
        when(realm.map()).thenReturn(realmMap);
        when(realm.id()).thenReturn(1);
        id = 1;
        jpaFixture = new JpaFixture();
        guildRepository = new GuildRepositoryImpl(jpaFixture.getEntityManagerFactory(), Mockito.mock(KungFuFactory.class));
        founder = Mockito.mock(Player.class);
        when(founder.getRealm()).thenReturn(realm);
        when(founder.id()).thenReturn(playerId++);
        stone = createStone(Coordinate.xy(1, 1), "test");
        stone.foundedBy(founder);
    }


    private AttackKungFu createGuildKungFu() {
        ApplyGuildKungFuInput test = ApplyGuildKungFuInput.builder()
                .name("test")
                .type(AttackKungFuType.SWORD)
                .build();
        kungFuFactory.registerAttackKungFuParameters(test);
        return kungFuFactory.createAttackKungFu("test");
    }


    private Guild createStone(Coordinate coordinate, String name) {
        return new Guild(id++, coordinate, 1000000, 1000000, name, null, 1);
    }

    private GuildPo selectStonePo() {
        return jpaFixture.newEntityManager().createQuery("select p from GuildPo p where p.id = ?1", GuildPo.class)
                .setParameter(1, stone.getGuildId()).getResultList().get(0);
    }

    @Test
    void save() {
        guildRepository.save(stone);
        var stonePo = jpaFixture.newEntityManager().createQuery("select p from GuildPo p where p.id = ?1", GuildPo.class)
                .setParameter(1, stone.getGuildId()).getResultList().get(0);
        assertNull(stonePo.getGuildKungFuPo());
        assertFalse(stonePo.getMembers().isEmpty());
        assertEquals(Coordinate.xy(1,1), stonePo.coordinate());
        assertEquals("test", stonePo.getName());
        assertEquals(realm.id(), stonePo.getRealmId());
    }

    @Test
    void applyGuildKungFu() {
        guildRepository.save(stone);
        AttackKungFu guildKungFu = createGuildKungFu();
        stone.registerGuildKungFu(guildKungFu);
        guildRepository.update(stone);
        var stonePo = jpaFixture.newEntityManager().createQuery("select p from GuildPo p where p.id = ?1", GuildPo.class)
                .setParameter(1, stone.getGuildId()).getResultList().get(0);
        assertNotNull(stonePo.getGuildKungFuPo());
    }

    @Test
    void delete() {
        guildRepository.save(stone);
        AttackKungFu guildKungFu = createGuildKungFu();
        stone.registerGuildKungFu(guildKungFu);
        guildRepository.update(stone);
        guildRepository.delete(stone.getGuildId());
        var kungFuPo = jpaFixture.newEntityManager().createQuery("select p from GuildKungFuPo p", GuildKungFuPo.class).getResultList().get(0);
        assertNotNull(kungFuPo);
    }

    private Player mockMember() {
        var player = Mockito.mock(Player.class);
        when(player.getRealm()).thenReturn(realm);
        when(player.id()).thenReturn(playerId++);
        return player;
    }
    @Test
    void addMember() {
        guildRepository.save(stone);
        stone.addMember(mockMember());
        guildRepository.update(stone);
        var stonePo = selectStonePo();
        assertEquals(2, stonePo.getMembers().size());
    }

    @Test
    void removeMember() {
        guildRepository.save(stone);
        Player m1 = mockMember();
        stone.addMember(m1);
        Player m2 = mockMember();
        stone.addMember(m2);
        guildRepository.update(stone);
        var stonePo = selectStonePo();
        assertEquals(3, stonePo.getMembers().size());

        stone.removeMember(m2.id());
        guildRepository.update(stone);
        stonePo = selectStonePo();
        assertEquals(2, stonePo.getMembers().size());
    }
}
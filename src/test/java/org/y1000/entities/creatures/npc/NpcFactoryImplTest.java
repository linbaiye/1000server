package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.MerchantItemSdbRepositoryImpl;
import org.y1000.sdb.MonstersSdbImpl;
import org.y1000.sdb.NpcSdbImpl;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;


class NpcFactoryImplTest {

    private final NpcFactoryImpl npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE,
            NpcSdbImpl.Instance, new MerchantItemSdbRepositoryImpl(ItemSdbImpl.INSTANCE));
    private RealmMap map;

    @BeforeEach
    void setUp() {
        map = Mockito.mock(RealmMap.class);
    }

    @Test
    void createNpc() {
        var npc = npcFactory.createNpc("一级牛", 1L, map, Coordinate.xy(2, 2));
        assertEquals("牛", npc.name());
        assertInstanceOf(PassiveMonster.class, npc);
        var lbn = npcFactory.createMerchant("一级老板娘", 1L, map, Coordinate.xy(2, 2));
        assertEquals("老板娘", lbn.name());
        var merchant = (DevirtueMerchant)npcFactory.createNpc("一级老板娘", 1L, map, Coordinate.xy(2, 2));
        assertEquals("老板娘", merchant.name());
    }

    @Test
    void createMonster() {
        var npc = npcFactory.createNpc("一级牛", 1L, map, Coordinate.xy(2, 2));
        assertEquals("牛", npc.name());
        assertEquals(Coordinate.xy(2, 2), npc.coordinate());
        assertEquals(Coordinate.xy(2, 2), npc.spawnCoordinate());
    }

    @Test
    void createSubmissiveNpc() {
        var npc = npcFactory.createNpc("稻草人", 1L, map, Coordinate.xy(2, 2));
        assertInstanceOf(SubmissiveNpc.class, npc);
        assertEquals(1, npc.id());
        assertEquals(Coordinate.xy(2, 2), npc.coordinate());
        assertEquals(State.IDLE, npc.stateEnum());
    }

    @Test
    void createGuardian() {
        var npc = npcFactory.createNpc("男卒兵", 3L, map, Coordinate.xy(2, 2));
        assertInstanceOf(Guardian.class, npc);
        assertEquals(3, npc.id());
        assertEquals(Coordinate.xy(2, 2), npc.coordinate());
        assertEquals(State.IDLE, npc.stateEnum());
    }
}
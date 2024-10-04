package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;


class NpcFactoryImplTest {

    private final NpcFactoryImpl npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE,
            NpcSdbImpl.Instance, MagicParamSdb.INSTANCE, new MerchantItemSdbRepositoryImpl(ItemSdbImpl.INSTANCE));
    private RealmMap map;

    @BeforeEach
    void setUp() {
        map = Mockito.mock(RealmMap.class);
    }

    @Test
    void createNpc() {
        var npc = npcFactory.createNpc("牛", 1L, map, Coordinate.xy(2, 2));
        assertEquals("牛", npc.viewName());
        assertInstanceOf(PassiveMonster.class, npc);
        var lbn = npcFactory.createMerchant("老板娘", 1L, map, Coordinate.xy(2, 2));
        assertEquals("老板娘", lbn.viewName());
        var merchant = (SubmissiveMerchant)npcFactory.createNpc("老板娘", 1L, map, Coordinate.xy(2, 2));
        assertEquals("老板娘", merchant.viewName());
    }

    @Test
    void createMonster() {
        var npc = npcFactory.createNpc("牛", 1L, map, Coordinate.xy(2, 2));
        assertEquals("牛", npc.viewName());
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

    @Test
    void createNpcWithSpells() {
        /*var npc = npcFactory.createNpc("白狐狸", 3L, map, Coordinate.xy(2, 2));
        TestingEventListener eventListener = new TestingEventListener();
        npc.registerEventListener(eventListener);
        Player player = Mockito.mock(Player.class);
        when(player.damage()).thenReturn(new Damage(1000000, 1000, 100, 100));
        when(player.hit()).thenReturn(1000000);
        while (npc.stateEnum() != State.DIE) {
            npc.attackedBy(player);
        }
        assertNotNull(eventListener.removeFirst(CreatureDieEvent.class));
        npc.update(npc.getStateMillis(State.DIE) + 2000);
        assertNotNull(eventListener.removeFirst(NpcShiftEvent.class));*/

        assertNotNull(npcFactory.createNpc("分身忍者", 3L, map, Coordinate.xy(2, 3)));
    }
}
package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.creatures.State;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class NpcFactoryImplTest extends AbstractUnitTestFixture  {

    private final NpcFactoryImpl npcFactory = createNpcFactory();
    private RealmMap map;

    private CreateNonMonsterSdb nonMonsterSdb;

    @BeforeEach
    void setUp() {
        nonMonsterSdb = Mockito.mock(CreateNonMonsterSdb.class);
        when(nonMonsterSdb.getMerchant(anyString())).thenReturn(Optional.empty());
        map = Mockito.mock(RealmMap.class);
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
        assertInstanceOf(Scarecrow.class, npc);
        assertEquals(1, npc.id());
        assertEquals(Coordinate.xy(2, 2), npc.coordinate());
        assertEquals(State.IDLE, npc.stateEnum());
    }

    @Test
    void createGuardian() {
        var npc = npcFactory.createNonMonsterNpc("男卒兵", 3L, map, Coordinate.xy(2, 2), nonMonsterSdb);
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
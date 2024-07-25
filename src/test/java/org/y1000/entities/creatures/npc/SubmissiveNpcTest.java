package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.CreatureInterpolationPacket;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SubmissiveNpcTest extends AbstractUnitTestFixture  {

    private SubmissiveNpc npc;

    private RealmMap mockedMap;

    @BeforeEach
    void setUp() {
        TestingMonsterAttributeProvider attributeProvider = new TestingMonsterAttributeProvider();
        attributeProvider.life = 1;
        attributeProvider.avoidance = 0;
        mockedMap = Mockito.mock(RealmMap.class);
        npc = SubmissiveNpc.builder()
                .id(1L)
                .name("test")
                .realmMap(mockedMap)
                .stateMillis(MONSTER_STATE_MILLIS)
                .direction(Direction.DOWN)
                .coordinate(Coordinate.xy(2, 3))
                .attributeProvider(attributeProvider)
                .ai(NpcFrozenAI.INSTANCE)
                .build();

    }

    @Test
    void interpolation() {
        CreatureInterpolationPacket creatureInterpolation = npc.captureInterpolation().toPacket().getCreatureInterpolation();
        assertEquals(NpcType.MONSTER.value(), creatureInterpolation.getType());
        assertEquals(1, creatureInterpolation.getId());
        assertEquals(2, creatureInterpolation.getInterpolation().getX());
        assertEquals(3, creatureInterpolation.getInterpolation().getY());
        assertEquals(Direction.DOWN.value(), creatureInterpolation.getInterpolation().getDirection());
    }

    @Test
    void revive() {
        PlayerImpl player = playerBuilder().coordinate(npc.coordinate().moveBy(Direction.RIGHT)).build();
        npc.attackedBy(player);
        assertEquals(State.DIE, npc.stateEnum());
        Mockito.reset(mockedMap);
        npc.respawn(npc.coordinate().move(0, 1));
        assertEquals(State.IDLE, npc.stateEnum());
        assertEquals(npc.coordinate(), Coordinate.xy(2, 4));
        assertEquals(npc.spawnCoordinate(), Coordinate.xy(2, 4));
        Mockito.verify(mockedMap, Mockito.times(1)).occupy(npc);
    }

    @Test
    void equalsAndHashCode() {
        Map<SubmissiveNpc, Integer> map = new HashMap<>();
        var copied = SubmissiveNpc.builder()
                .id(1L)
                .name("test")
                .realmMap(mockedMap)
                .stateMillis(MONSTER_STATE_MILLIS)
                .direction(Direction.DOWN)
                .coordinate(Coordinate.xy(2, 3))
                .attributeProvider(new TestingMonsterAttributeProvider())
                .ai(NpcFrozenAI.INSTANCE)
                .build();
        map.put(npc, 1);
        assertEquals(1, map.get(copied));
    }

    @Test
    void removeSelfWhenDone() {
        TestingEventListener eventListener = new TestingEventListener();
        npc.registerEventListener(eventListener);
        PlayerImpl player = playerBuilder().coordinate(npc.coordinate().moveBy(Direction.RIGHT)).build();
        npc.attackedBy(player);
        npc.update(npc.getStateMillis(State.DIE) + 8000);
        RemoveEntityEvent removeEntityEvent = eventListener.removeFirst(RemoveEntityEvent.class);
        assertEquals(npc.id(), removeEntityEvent.toPacket().getRemoveEntity().getId());
        Mockito.verify(mockedMap, Mockito.times(1)).free(npc);
    }
}
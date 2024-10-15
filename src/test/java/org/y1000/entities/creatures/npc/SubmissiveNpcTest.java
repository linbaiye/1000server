package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.AI.SubmissiveWanderingAI;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.CreatureInterpolationPacket;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SubmissiveNpcTest extends AbstractNpcUnitTestFixture {

    private SubmissiveNpc npc;

    private RealmMap mockedMap;

    private TestingMonsterAttributeProvider attributeProvider;

    @BeforeEach
    void setUp() {
        attributeProvider = new TestingMonsterAttributeProvider();
        attributeProvider.life = 1;
        attributeProvider.avoidance = 0;
        mockedMap = Mockito.mock(RealmMap.class);
        npc = createNpc(NpcFrozenAI.INSTANCE);
    }

    private SubmissiveNpc createNpc(NpcAI ai) {
        return SubmissiveNpc.builder()
                .id(1L)
                .name("test")
                .realmMap(mockedMap)
                .stateMillis(MONSTER_STATE_MILLIS)
                .coordinate(Coordinate.xy(2, 3))
                .attributeProvider(attributeProvider)
                .ai(ai)
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
    void equalsAndHashCode() {
        Map<SubmissiveNpc, Integer> map = new HashMap<>();
        var copied = SubmissiveNpc.builder()
                .id(1L)
                .name("test")
                .realmMap(mockedMap)
                .stateMillis(MONSTER_STATE_MILLIS)
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

    @Test
    void attackedByPlayer() {
        TestingEventListener eventListener = new TestingEventListener();
        attributeProvider.life = 1000;
        npc = createNpc(new SubmissiveWanderingAI());
        npc.registerEventListener(eventListener);
        Player player = mockEnemyPlayer(npc.coordinate());
        while (!npc.attackedBy(player)) ;
        assertEquals(State.HURT, npc.stateEnum());
        npc.update(npc.getStateMillis(State.HURT));
        assertEquals(State.IDLE, npc.stateEnum());
    }
}
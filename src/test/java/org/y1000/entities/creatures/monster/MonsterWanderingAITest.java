package org.y1000.entities.creatures.monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;
import org.y1000.entities.players.Player;
import org.y1000.message.SetPositionEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MonsterWanderingAITest extends AbstractMonsterUnitTestFixture {
    private MonsterWanderingAI ai;
    @BeforeEach
    void setUp() {
        setup();
        ai = (MonsterWanderingAI) monster.AI();
    }

    @Test
    void start() {
        ai.start(monster);
        assertEquals(State.IDLE, monster.stateEnum());
        assertInstanceOf(MonsterCommonState.class, monster.state());
        assertNotNull(eventListener.removeFirst(MonsterChangeStateEvent.class));
    }

    @Test
    void onIdleDone() {
        ai.onIdleDone(monster);
        assertEquals(State.FROZEN, monster.stateEnum());
        assertInstanceOf(MonsterCommonState.class, monster.state());
        assertNotNull(eventListener.removeFirst(MonsterChangeStateEvent.class));
    }

    @Test
    void move() {
        ai.onMoveDone(monster);
        assertEquals(State.IDLE, monster.stateEnum());
        assertInstanceOf(MonsterCommonState.class, monster.state());
        assertNotNull(eventListener.removeFirst(MonsterChangeStateEvent.class));
    }

    @Test
    void onMoveFailed() {
        ai.onMoveFailed(monster);
        assertEquals(State.IDLE, monster.stateEnum());
        assertInstanceOf(MonsterCommonState.class, monster.state());
        assertNotNull(eventListener.removeFirst(SetPositionEvent.class));
        assertNotNull(eventListener.removeFirst(MonsterChangeStateEvent.class));
    }

    @Test
    void onHurtDone() {
        var player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(monster.coordinate().move(3, 4));
        monster.setFightingEntity(player);
        ai.onHurtDone(monster);
        assertEquals(State.IDLE, monster.stateEnum());
        assertNotEquals(monster.AI(), ai);
    }
}
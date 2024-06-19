package org.y1000.entities.creatures.monster.wander;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.monster.fight.MonsterFightIdleState;
import org.y1000.entities.players.Player;
import org.y1000.message.MoveEvent;
import org.y1000.message.PositionType;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class MonsterWanderingFrozenStateTest extends AbstractMonsterUnitTestFixture {


    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void makeTurn() {
        monster = monsterBuilder().coordinate(Coordinate.xy(1, 1)).direction(Direction.LEFT).build();
        monster.registerEventListener(eventListener);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        MonsterWanderingFrozenState state = MonsterWanderingFrozenState.Freeze(monster, Coordinate.xy(2, 1), monster.coordinate());
        monster.changeState(state);
        monster.update(monster.getStateMillis(State.FROZEN));
        assertTrue(monster.state() instanceof MonsterWanderingIdleState);
        assertEquals(monster.direction(), Direction.RIGHT);
        SetPositionEvent event = eventListener.dequeue(SetPositionEvent.class);
        assertEquals(event.toPacket().getPositionPacket().getState(), State.IDLE.value());
    }

    @Test
    void moveForwards() {
        monster = monsterBuilder().coordinate(Coordinate.xy(1, 1)).direction(Direction.LEFT).build();
        monster.registerEventListener(eventListener);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        MonsterWanderingFrozenState state = MonsterWanderingFrozenState.Freeze(monster, Coordinate.xy(0, 1), monster.coordinate());
        monster.changeState(state);
        monster.update(monster.getStateMillis(State.FROZEN));
        assertTrue(monster.state() instanceof MonsterWanderingMoveState);
        MoveEvent event = eventListener.dequeue(MoveEvent.class);
        assertEquals(event.toPacket().getPositionPacket().getType(), PositionType.MOVE.value());
    }

    @Test
    void destinationNotMovable() {
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(false);
        MonsterWanderingFrozenState state = MonsterWanderingFrozenState.Freeze(monster, Coordinate.xy(0, 1), monster.coordinate());
        monster.changeState(state);
        monster.update(monster.getStateMillis(State.FROZEN));
        assertTrue(monster.state() instanceof MonsterWanderingIdleState);
        MonsterChangeStateEvent event = eventListener.dequeue(MonsterChangeStateEvent.class);
        assertNotNull(event);
    }

    @Test
    void getHurt() {
        Player player = playerBuilder().build();
        monster.attackedBy(player);
        monster.update(monster.getStateMillis(State.HURT));
        assertTrue(monster.state() instanceof MonsterFightIdleState);
    }
}
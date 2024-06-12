package org.y1000.entities.creatures.monster.wander;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonsterWanderingMoveStateTest extends AbstractMonsterUnitTestFixture {


    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void move() {
        Coordinate coordinate = monster.coordinate();
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        MonsterWanderingMoveState moveState = MonsterWanderingMoveState.move(monster, monster.coordinate().moveBy(monster.direction()));
        monster.changeState(moveState);
        monster.update(monster.getStateMillis(State.WALK));
        assertEquals(monster.coordinate(), coordinate.moveBy(monster.direction()));
        assertTrue(monster.state() instanceof MonsterWanderingIdleState);
        CreatureChangeStateEvent dequeue = eventListener.dequeue(CreatureChangeStateEvent.class);
        assertEquals(dequeue.toPacket().getChangeStatePacket().getState(), State.IDLE.value());
        verify(monster.realmMap()).free(monster);
        verify(monster.realmMap()).occupy(monster);
    }

    @Test
    void notMovable() {
        Coordinate coordinate = monster.coordinate();
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(false);
        MonsterWanderingMoveState moveState = MonsterWanderingMoveState.move(monster, monster.coordinate().moveBy(monster.direction()));
        monster.changeState(moveState);
        monster.update(monster.getStateMillis(State.WALK));
        assertEquals(monster.coordinate(), coordinate);
        assertTrue(monster.state() instanceof MonsterWanderingIdleState);
        SetPositionEvent event = eventListener.dequeue(SetPositionEvent.class);
        assertEquals(event.toPacket().getPositionPacket().getX(), coordinate.x());
        assertEquals(event.toPacket().getPositionPacket().getY(), coordinate.y());
    }
}
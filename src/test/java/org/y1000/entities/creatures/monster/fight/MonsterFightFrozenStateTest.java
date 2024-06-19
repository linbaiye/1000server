package org.y1000.entities.creatures.monster.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.monster.wander.MonsterWanderingIdleState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.MoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MonsterFightFrozenStateTest extends AbstractMonsterUnitTestFixture {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void nextMoveWhenTargetFar() {
        // Moving right.
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        MonsterFightFrozenState frozenState = MonsterFightFrozenState.next(monster, monster.coordinate().moveBy(Direction.LEFT));
        PlayerImpl player = playerBuilder().coordinate(monster.coordinate().move(2, 0)).build();
        monster.setFightingEntity(player);
        monster.changeState(frozenState);
        monster.update(monster.getStateMillis(State.FROZEN));
        assertTrue(monster.state() instanceof MonsterFightMoveState);
        assertNotNull(eventListener.dequeue(MoveEvent.class));
    }

    @Test
    void nextMoveWhenTargetAdjacent() {
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(false);
        MonsterFightFrozenState frozenState = MonsterFightFrozenState.next(monster, monster.coordinate().moveBy(Direction.LEFT));
        PlayerImpl player = playerBuilder().coordinate(monster.coordinate().move(1, 0)).build();
        monster.setFightingEntity(player);
        monster.changeState(frozenState);
        monster.update(monster.getStateMillis(State.FROZEN));
        assertTrue(monster.state() instanceof MonsterFightAttackState);
        assertNotNull(eventListener.dequeue(CreatureAttackEvent.class));
    }

    @Test
    void nextMoveWhenFrontNotMovable() {
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenAnswer((Answer<Boolean>) invocationOnMock -> {
            Coordinate coordinate = invocationOnMock.getArgument(0);
            return !coordinate.equals(monster.coordinate().moveBy(Direction.RIGHT));
        });
        // ahead
        MonsterFightFrozenState frozenState = MonsterFightFrozenState.next(monster, monster.coordinate().moveBy(Direction.LEFT));
        PlayerImpl player = playerBuilder().coordinate(monster.coordinate().move(2, 0)).build();
        monster.setFightingEntity(player);
        monster.changeState(frozenState);
        monster.update(monster.getStateMillis(State.FROZEN));
        assertTrue(monster.state() instanceof MonsterFightIdleState);
        assertNotNull(eventListener.dequeue(SetPositionEvent.class));
    }

    @Test
    void nextMoveWhenTargetGone() {
        monster.changeDirection(Direction.RIGHT);
        MonsterFightFrozenState frozenState = MonsterFightFrozenState.next(monster, monster.coordinate().moveBy(Direction.LEFT));
        PlayerImpl player = playerBuilder().coordinate(monster.coordinate().move(2, 0)).build();
        monster.setFightingEntity(player);
        monster.changeState(frozenState);
        monster.update(monster.getStateMillis(State.FROZEN) - 1);
        player.leaveRealm();
        monster.update(2);
        assertTrue(monster.state() instanceof MonsterWanderingIdleState);
        assertNotNull(eventListener.dequeue(MonsterChangeStateEvent.class));
    }

    @Test
    void getHurt() {
        MonsterFightFrozenState frozenState = MonsterFightFrozenState.next(monster, monster.coordinate().moveBy(Direction.LEFT));
        monster.changeState(frozenState);
        PlayerImpl player = playerBuilder().build();
        monster.attackedBy(player);
        assertTrue(monster.state() instanceof MonsterHurtState);
        assertNotNull(eventListener.dequeue(CreatureHurtEvent.class));
        monster.update(monster.getStateMillis(State.HURT));
        assertTrue(monster.state() instanceof MonsterFightFrozenState);
    }

    @Test
    void getHurtWhenStateAlmostOver() {
        monster.changeDirection(Direction.RIGHT);
        monster.changeCoordinate(Coordinate.xy(1, 1));
        MonsterFightFrozenState frozenState = MonsterFightFrozenState.next(monster, monster.coordinate().moveBy(Direction.LEFT));
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        monster.changeState(frozenState);
        monster.update(monster.getStateMillis(State.FROZEN) - 1);
        PlayerImpl player = playerBuilder().coordinate(Coordinate.xy(1, 2)).build();
        monster.setFightingEntity(player);
        monster.attackedBy(player);
        assertTrue(monster.state() instanceof MonsterHurtState);
        monster.update(monster.getStateMillis(State.HURT));
        assertEquals(Direction.DOWN, monster.direction());
    }
}
package org.y1000.entities.creatures.monster.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MonsterFightMoveStateTest extends AbstractMonsterUnitTestFixture {
    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void attackIfMovedToAdjacent() {
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        MonsterFightMoveState moveState = MonsterFightMoveState.towardsCurrentDirection(monster);
        monster.changeState(moveState);
        monster.setFightingEntity(playerBuilder().coordinate(monster.coordinate().move(2, 0)).build());
        monster.update(monster.getStateMillis(State.WALK));
        assertTrue(monster.state() instanceof MonsterFightAttackState);
        assertNotNull(eventListener.dequeue(CreatureAttackEvent.class));
    }

    @Test
    void backToIdleIfNotMovable() {
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(false);
        MonsterFightMoveState moveState = MonsterFightMoveState.towardsCurrentDirection(monster);
        monster.changeState(moveState);
        monster.update(monster.getStateMillis(State.WALK));
        assertTrue(monster.state() instanceof MonsterFightIdleState);
    }

    @Test
    void keepMovingIfTargetFar() {
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        MonsterFightMoveState moveState = MonsterFightMoveState.towardsCurrentDirection(monster);
        monster.changeState(moveState);
        monster.setFightingEntity(playerBuilder().coordinate(monster.coordinate().move(3, 0)).build());
        monster.update(monster.getStateMillis(State.WALK));
        assertTrue(monster.state() instanceof MonsterFightIdleState);
        assertNotNull(eventListener.dequeue(MonsterChangeStateEvent.class));
    }

    @Test
    void moveAheadWhenGettingHurt() {
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        MonsterFightMoveState moveState = MonsterFightMoveState.towardsCurrentDirection(monster);
        monster.changeState(moveState);
        monster.attackedBy(playerBuilder().build());
        assertEquals(monster.coordinate(), Coordinate.xy(2, 1));
        assertTrue(monster.state() instanceof MonsterHurtState);
        assertNotNull(eventListener.dequeue(CreatureHurtEvent.class));
    }

    @Test
    void afterHurtWhenAheadNotMovable() {
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(false);
        MonsterFightMoveState moveState = MonsterFightMoveState.towardsCurrentDirection(monster);
        monster.changeState(moveState);
        monster.attackedBy(playerBuilder().coordinate(Coordinate.xy(1, 2)).build());
        assertEquals(monster.coordinate(), Coordinate.xy(1, 1));
        assertTrue(monster.state() instanceof MonsterHurtState);
        monster.update(monster.getStateMillis(State.HURT));
        // ready to attack.
        assertTrue(monster.state() instanceof MonsterFightCooldownState);
    }
}
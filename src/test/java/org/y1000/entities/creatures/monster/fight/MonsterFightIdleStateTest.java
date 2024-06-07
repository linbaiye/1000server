package org.y1000.entities.creatures.monster.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.monster.wander.MonsterWanderingIdleState;
import org.y1000.entities.players.PlayerImpl;

import static org.junit.jupiter.api.Assertions.*;

class MonsterFightIdleStateTest extends AbstractMonsterUnitTestFixture {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void attackIfCloseEnough() {
        MonsterFightIdleState state = MonsterFightIdleState.start(monster);
        PlayerImpl player = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.LEFT)).build();
        monster.changeState(state);
        monster.setFightingEntity(player);
        monster.update(monster.getStateMillis(State.IDLE));
        assertTrue(monster.state() instanceof MonsterFightAttackState);
        CreatureAttackEvent event = eventListener.dequeue(CreatureAttackEvent.class);
        assertNotNull(event);
    }

    @Test
    void freezeIfFar() {
        MonsterFightIdleState state = MonsterFightIdleState.start(monster);
        PlayerImpl player = playerBuilder().coordinate(monster.coordinate().move(2, 0)).build();
        monster.changeState(state);
        monster.setFightingEntity(player);
        monster.update(monster.getStateMillis(State.IDLE));
        assertTrue(monster.state() instanceof MonsterFightFrozenState);
        var event = eventListener.dequeue(CreatureChangeStateEvent.class);
        assertNotNull(event);
    }

    @Test
    void backToWanderingIfFightOver() {
        MonsterFightIdleState state = MonsterFightIdleState.start(monster);
        PlayerImpl player = playerBuilder().build();
        monster.setFightingEntity(player);
        monster.changeState(state);
        player.leaveRealm();
        monster.update(monster.getStateMillis(State.IDLE));
        assertTrue(monster.state() instanceof MonsterWanderingIdleState);
        var event = eventListener.dequeue(CreatureChangeStateEvent.class);
        assertNotNull(event);
    }

    @Test
    void getHurt() {
        MonsterFightIdleState state = MonsterFightIdleState.start(monster);
        monster.changeState(state);
        PlayerImpl player = playerBuilder().build();
        monster.attackedBy(player);
        assertTrue(monster.state() instanceof MonsterHurtState);
        assertNotNull(eventListener.dequeue(CreatureHurtEvent.class));

        monster.update(monster.getStateMillis(State.HURT));
        assertTrue(monster.state() instanceof MonsterFightIdleState);
        MonsterFightIdleState idleState = (MonsterFightIdleState)monster.state();
        assertEquals(idleState.elapsedMillis(), monster.getStateMillis(State.HURT));
    }

    @Test
    void getHurtWhenStateAlmostOver() {
        MonsterFightIdleState state = MonsterFightIdleState.start(monster);
        monster.changeState(state);
        // one more millisecond to finish this state.
        monster.update(monster.getStateMillis(State.IDLE) - 1);

        PlayerImpl player = playerBuilder().build();
        monster.attackedBy(player);
        assertTrue(monster.state() instanceof MonsterHurtState);
        monster.update(monster.getStateMillis(State.HURT));
        assertTrue(monster.state() instanceof MonsterFightCooldownState);
    }
}
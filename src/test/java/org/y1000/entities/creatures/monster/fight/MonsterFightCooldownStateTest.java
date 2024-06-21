package org.y1000.entities.creatures.monster.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.players.Player;
import org.y1000.realm.Realm;

import static org.junit.jupiter.api.Assertions.*;

class MonsterFightCooldownStateTest extends AbstractMonsterUnitTestFixture {


    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void getHurt() {
        MonsterFightCooldownState cooldownState = MonsterFightCooldownState.cooldown(10 * Realm.STEP_MILLIS);
        Player player = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.RIGHT)).build();
        monster.setFightingEntity(player);
        monster.changeState(cooldownState);
        monster.attackedBy(player);
        assertEquals(attributeProvider.recovery() * Realm.STEP_MILLIS, monster.cooldown());
        assertTrue(monster.state() instanceof MonsterHurtState);
        monster.update(monster.getStateMillis(State.HURT));
        assertTrue(monster.state() instanceof MonsterFightCooldownState);
        assertEquals(attributeProvider.recovery() * Realm.STEP_MILLIS - monster.getStateMillis(State.HURT), monster.cooldown());
    }

    @Test
    void nextMove() {
        MonsterFightCooldownState cooldownState = MonsterFightCooldownState.cooldown(10);
        monster.changeState(cooldownState);
        monster.setFightingEntity(playerBuilder().coordinate(monster.coordinate().move(1, 0)).build());
        monster.update(10);
        assertTrue(monster.state() instanceof MonsterFightAttackState);
        assertNotNull(eventListener.dequeue(CreatureAttackEvent.class));
    }
}
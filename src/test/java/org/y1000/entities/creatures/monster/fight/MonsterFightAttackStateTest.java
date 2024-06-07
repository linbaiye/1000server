package org.y1000.entities.creatures.monster.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;

import static org.junit.jupiter.api.Assertions.*;

class MonsterFightAttackStateTest extends AbstractMonsterUnitTestFixture {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void nextMove() {
        monster = monsterBuilder().attackSpeed(99).build();
        monster.registerEventListener(eventListener);
        MonsterFightAttackState attackState = MonsterFightAttackState.of(monster);
        monster.setFightingEntity(playerBuilder().coordinate(monster.coordinate().move(1, 0)).build());
        monster.changeState(attackState);
        monster.cooldownAttack();
        monster.update(monster.getStateMillis(State.ATTACK));
        assertTrue(monster.state() instanceof MonsterFightCooldownState);
        assertNotNull(eventListener.dequeue(CreatureChangeStateEvent.class));
    }
}
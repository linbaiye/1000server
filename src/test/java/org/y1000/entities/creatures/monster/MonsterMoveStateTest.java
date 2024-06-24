package org.y1000.entities.creatures.monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.creatures.State;

import static org.junit.jupiter.api.Assertions.*;

class MonsterMoveStateTest extends AbstractMonsterUnitTestFixture {
    private MonsterMoveState moveState;


    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void createWithSpeedRate() {
        int speed = monster.getStateMillis(State.WALK) / 2;
        moveState = MonsterMoveState.move(monster, speed);
        assertEquals(moveState.totalMillis(), speed);
    }
}
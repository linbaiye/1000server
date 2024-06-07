package org.y1000.entities.creatures.monster.wander;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.monster.fight.MonsterFightIdleState;
import org.y1000.entities.players.Player;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonsterWanderingIdleStateTest extends AbstractMonsterUnitTestFixture  {


    @BeforeEach
    public void setUp() {
        setup();
    }

    @Test
    void nextMove() {
        assertTrue(monster.state() instanceof MonsterWanderingIdleState);
        monster.update(monster.getStateMillis(State.IDLE));
        assertTrue(monster.state() instanceof MonsterWanderingFrozenState);
        assertNotNull(eventListener.dequeue(CreatureChangeStateEvent.class));
    }

    @Test
    void getHurt() {
        Player player = playerBuilder().build();
        monster.attackedBy(player);
        monster.update(monster.getStateMillis(State.HURT));
        assertTrue(monster.state() instanceof MonsterFightIdleState);
    }
}
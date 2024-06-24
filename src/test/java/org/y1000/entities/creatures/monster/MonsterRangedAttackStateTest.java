package org.y1000.entities.creatures.monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.MonsterShootEvent;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonsterRangedAttackStateTest extends AbstractMonsterUnitTestFixture {

    private MonsterRangedAttackState attackState;

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void shoot() {
        Player player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(monster.coordinate().move(1, 2));
        monster.setFightingEntity(player);
        attackState = MonsterRangedAttackState.attack(monster, "test", 1);
        attackState.update(monster, monster.getStateMillis(State.ATTACK) / 2 - 1);
        assertNull(eventListener.removeFirst(MonsterShootEvent.class));
        attackState.update(monster, 2);
        assertNotNull(eventListener.removeFirst(MonsterShootEvent.class));
    }

    @Test
    void afterHurt() {
        attackState = MonsterRangedAttackState.attack(monster, "test", 1);
        var ai = Mockito.mock(MonsterAI.class);
        monster.changeAI(ai);
        attackState.afterHurt(monster);
        verify(ai).onAttackDone(any(AbstractMonster.class));
    }
}
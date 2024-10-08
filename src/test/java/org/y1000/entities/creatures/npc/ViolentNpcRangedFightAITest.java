package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.npc.AI.ViolentNpcRangedFightAI;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ViolentNpcRangedFightAITest extends AbstractMonsterUnitTestFixture  {

    private Creature enemy;

    private ViolentNpcRangedFightAI ai;

    @BeforeEach
    void setUp() {
        setup();
        NpcRangedSkill skill = new NpcRangedSkill(1, "");
        monster = monsterBuilder().skill(skill).build();
        enemy = Mockito.mock(Creature.class);
        when(enemy.canBeAttackedNow()).thenReturn(true);
        when(enemy.canBeSeenAt(any(Coordinate.class))).thenReturn(true);
        ai = new ViolentNpcRangedFightAI(enemy, monster);
    }

    @Test
    void whenInShootRange() {
        Coordinate coordinate = monster.coordinate().move(4, 0);
        when(enemy.coordinate()).thenReturn(coordinate);
        monster.changeAndStartAI(ai);
        assertEquals(State.ATTACK, monster.stateEnum());
    }

    @Test
    void escape() {
        monster.changeDirection(Direction.RIGHT);
        when(monster.realmMap().movable(any(Coordinate.class))).thenReturn(true);
        Coordinate coordinate = monster.coordinate().moveBy(monster.direction());
        when(enemy.coordinate()).thenReturn(coordinate);
        monster.changeAndStartAI(ai);
        assertEquals(Direction.LEFT, monster.direction());
        assertEquals(State.IDLE, monster.stateEnum());
        monster.update(monster.getStateMillis(State.IDLE));
        assertEquals(Direction.LEFT, monster.direction());
        assertEquals(State.WALK, monster.stateEnum());
        monster.update(monster.getStateMillis(State.WALK));
        assertEquals(Direction.LEFT, monster.direction());
        assertEquals(State.WALK, monster.stateEnum());

        monster.update(monster.getStateMillis(State.WALK));
        assertEquals(Direction.RIGHT, monster.direction());
        assertEquals(State.ATTACK, monster.stateEnum());
    }
}
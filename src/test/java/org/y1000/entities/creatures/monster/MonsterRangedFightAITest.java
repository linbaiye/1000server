package org.y1000.entities.creatures.monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MonsterRangedFightAITest extends AbstractMonsterUnitTestFixture {
    
    private MonsterRangedFightAI ai;

    @BeforeEach
    void setUp() {
        setup();
        monster = monsterBuilder().realmMap(realmMap).attackSkill(new MonsterRangedAttackSkill(1, "h")).build();
        ai = new MonsterRangedFightAI((MonsterRangedAttackSkill) monster.attackSkill());
        monster.changeAI(ai);
    }

}
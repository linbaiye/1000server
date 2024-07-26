package org.y1000.realm;

import org.junit.jupiter.api.Test;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RelevantScopeTest extends AbstractMonsterUnitTestFixture {


    private PassiveMonster createMonster(int x, int y) {
        return monsterBuilder().coordinate(Coordinate.xy(x, y)).build();
    }

    private PassiveMonster createMonster(Coordinate coordinate) {
        return monsterBuilder().coordinate(coordinate).build();
    }


    @Test
    void outOfScope() {
        Entity entity = createMonster(0, 0);
        RelevantScope relevantScope = new RelevantScope(entity);
        Entity another = createMonster(16, 16);
        assertTrue(relevantScope.outOfScope(another));
        another = createMonster(15, 15);
        assertFalse(relevantScope.outOfScope(another));
    }

    @Test
    void addIfVisible() {
        Entity entity = createMonster(0, 0);
        RelevantScope relevantScope = new RelevantScope(entity);
        assertFalse(relevantScope.addIfVisible(createMonster(new Coordinate(16, 16))));
        Entity another = createMonster(new Coordinate(15, 15));
        assertTrue(relevantScope.addIfVisible(another));
        assertFalse(relevantScope.addIfVisible(another));
    }

    @Test
    void removeIfNotVisible() {
        Entity entity = createMonster(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity);
        PassiveMonster entity1 = createMonster(new Coordinate(1, 2));
        relevantScope.addIfVisible(entity1);
        entity1.changeCoordinate(new Coordinate(15, 16));
        assertTrue(relevantScope.removeIfNotVisible(entity1));
    }

    @Test
    void update() {
        Entity entity = createMonster(new Coordinate(0, 0));
        RelevantScope relevantScope = new RelevantScope(entity);
        Entity entity1 = createMonster(new Coordinate(1, 2));
        relevantScope.addIfVisible(entity1);
        PassiveMonster entity2 = createMonster(new Coordinate(2, 2));
        relevantScope.addIfVisible(entity2);
        assertEquals(2, relevantScope.filter(ActiveEntity.class).size());
        entity2.changeCoordinate(new Coordinate(16, 16));
        Set<Entity> removed = relevantScope.update();
        assertTrue(removed.contains(entity2));
        assertEquals(1, relevantScope.filter(ActiveEntity.class).size());
    }
}
package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RelevantScopeManagerTest extends AbstractUnitTestFixture  {
    private AOIManager relevantScopeManager;
    @BeforeEach
    void setUp() {
        relevantScopeManager = new RelevantScopeManager();
    }

    private PassiveMonster createMonster(Coordinate coordinate) {
        return monsterBuilder().coordinate(coordinate).build();
    }


    @Test
    void add() {
        Entity entity = createMonster(new Coordinate(10, 10));
        relevantScopeManager.add(entity);
        Entity entity1 = createMonster(new Coordinate(10, 11));
        Set<Entity> affected = relevantScopeManager.add(entity1);
        assertTrue(affected.contains(entity));
        Entity entity2 = createMonster(new Coordinate(10, 12));
        Set<Entity> affected2 = relevantScopeManager.add(entity2);
        assertTrue(affected2.contains(entity1));
        assertTrue(affected2.contains(entity));
        Entity entity3 = createMonster(new Coordinate(entity.coordinate().x(), entity.coordinate().y() + Entity.VISIBLE_Y_RANGE + 1));
        Set<Entity> affected3 = relevantScopeManager.add(entity3);
        assertFalse(affected3.contains(entity));
        assertTrue(affected3.contains(entity1));
    }

    @Test
    void update() {
        PassiveMonster entity1 = createMonster(new Coordinate(10, 10));
        relevantScopeManager.add(entity1);
        Entity entity2 = createMonster(new Coordinate(10, 11));
        relevantScopeManager.add(entity2);
        Entity entity3 = createMonster(new Coordinate(10, 11 + Entity.VISIBLE_Y_RANGE + 2));
        relevantScopeManager.add(entity3);
        Set<Entity> affected = relevantScopeManager.update(entity1);
        assertTrue(affected.isEmpty());
        entity1.changeCoordinate(Coordinate.xy(10, 11 + Entity.VISIBLE_Y_RANGE + 1));
        affected = relevantScopeManager.update(entity1);
        assertTrue(affected.contains(entity2));
        assertTrue(affected.contains(entity3));
    }
}
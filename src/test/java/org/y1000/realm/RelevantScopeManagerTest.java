package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.Entity;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RelevantScopeManagerTest extends AbstractUnitTestFixture  {
    private RelevantScopeManager relevantScopeManager;
    @BeforeEach
    void setUp() {
        relevantScopeManager = new RelevantScopeManager();
    }

    private PassiveMonster createMonster(Coordinate coordinate) {
        return monsterBuilder().coordinate(coordinate).build();
    }


    @Test
    void add() {
        PhysicalEntity entity = createMonster(new Coordinate(10, 10));
        relevantScopeManager.add(entity);
        PhysicalEntity entity1 = createMonster(new Coordinate(10, 11));
        Set<PhysicalEntity> affected = relevantScopeManager.add(entity1);
        assertTrue(affected.contains(entity));
        PhysicalEntity entity2 = createMonster(new Coordinate(10, 12));
        Set<PhysicalEntity> affected2 = relevantScopeManager.add(entity2);
        assertTrue(affected2.contains(entity1));
        assertTrue(affected2.contains(entity));
        PhysicalEntity entity3 = createMonster(new Coordinate(entity.coordinate().x(), entity.coordinate().y() + Entity.VISIBLE_Y_RANGE + 1));
        Set<PhysicalEntity> affected3 = relevantScopeManager.add(entity3);
        assertFalse(affected3.contains(entity));
        assertTrue(affected3.contains(entity1));
    }

    @Test
    void update() {
        PassiveMonster entity1 = createMonster(new Coordinate(10, 10));
        relevantScopeManager.add(entity1);
        PhysicalEntity entity2 = createMonster(new Coordinate(10, 11));
        relevantScopeManager.add(entity2);
        PhysicalEntity entity3 = createMonster(new Coordinate(10, 11 + Entity.VISIBLE_Y_RANGE + 2));
        relevantScopeManager.add(entity3);
        Set<PhysicalEntity> affected = relevantScopeManager.update(entity1);
        assertTrue(affected.isEmpty());
        entity1.changeCoordinate(Coordinate.xy(10, 11 + Entity.VISIBLE_Y_RANGE + 1));
        affected = relevantScopeManager.update(entity1);
        assertTrue(affected.contains(entity2));
        assertTrue(affected.contains(entity3));
    }
}
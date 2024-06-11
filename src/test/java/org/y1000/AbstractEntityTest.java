package org.y1000;

import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.mockito.Mockito.when;

public abstract class AbstractEntityTest {

    private int id = 0;

    protected PassiveMonster createMonster(Coordinate coordinate) {
        return PassiveMonster.builder()
                .id(id++)
                .coordinate(coordinate)
                .direction(Direction.UP)
                .name("test")
                .realmMap(Mockito.mock(RealmMap.class))
                .wanderingRange(10)
                .build();
    }
}

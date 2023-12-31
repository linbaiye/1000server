package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.PhysicalEntity;
import org.y1000.util.Coordinate;

import java.util.Set;

public interface Creature extends PhysicalEntity {

    Coordinate coordinate();

    Direction direction();

    @Override
    default Set<Coordinate> occupyingCoordinates() {
        return Set.of(coordinate());
    }

}

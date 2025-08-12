package org.y1000.entities;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface Creature extends ActiveEntity  {

    Direction direction();

    void changeDirection(Direction newDirection);

    void changeCoordinate(Coordinate coordinate);

    String viewName();

    RealmMap realmMap();
}

package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.Optional;

public interface Monster extends ViolentCreature {
    void revive(Coordinate coordinate);

    int walkSpeed();

    RealmMap realmMap();

    Rectangle wanderingArea();

    Optional<String> normalSound();
}

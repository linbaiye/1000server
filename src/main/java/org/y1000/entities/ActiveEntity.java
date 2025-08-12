package org.y1000.entities;

import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


public interface ActiveEntity extends Entity {

    void update(int delta);

    <AB> Optional<AB> findAbility(Class<AB> type);

    default Set<Entity> getEntitiesAt(Set<Coordinate> coordinates) {
        return Collections.emptySet();
    }

    default Optional<String> clickText() {
        return Optional.empty();
    }



}

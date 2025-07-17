package org.y1000.entities;

import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


public interface ActiveEntity extends Entity {

    void update(int delta);

    void emitEvent(EntityEvent event);

    void registerEventListener(EntityEventListener listener);

    void deregisterEventListener(EntityEventListener listener);

    void clearListeners();

    <AB> Optional<AB> findAbility(Class<AB> type);

    default Set<Entity> getEntitiesAt(Set<Coordinate> coordinates) {
        return Collections.emptySet();
    }

}

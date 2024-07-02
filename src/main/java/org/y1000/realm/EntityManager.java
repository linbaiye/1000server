package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.event.EntityEventListener;

import java.util.Optional;

public interface EntityManager<T extends Entity> extends EntityEventListener {
    void update(long delta);

    Optional<T> find(long id);

    <N extends Entity> Optional<N> find(long id, Class<N> type);
}

package org.y1000.realm;

import org.y1000.entities.ActiveEntity;
import org.y1000.event.EntityEventListener;

import java.util.Optional;

public interface ActiveEntityManager<T extends ActiveEntity> extends EntityEventListener {

    void update(long delta);

    Optional<T> find(long id);

    default <N extends ActiveEntity> Optional<N> find(long id, Class<N> type) {
        return find(id).filter(t -> type.isAssignableFrom(t.getClass()))
                .map(type::cast);
    }

    boolean contains(T entity);
}

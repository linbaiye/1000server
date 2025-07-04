package org.y1000.realm;

import org.y1000.entities.Entity;

import java.util.Collections;
import java.util.Set;

public interface AOIManager {
    /**
     * Add a new entity and return visible entities if successful.
     * @param entity entity to add.
     * @return visible entities or empty if previously added.
     * @throws NullPointerException if entity null.
     */
    Set<Entity> add(Entity entity);

    boolean contains(Entity entity);

    <E extends Entity> Set<E> filterNoSelfVisibleEntities(Entity entity, Class<E> type);

    default <E extends Entity> Set<E> filterVisibleEntities(Entity entity, Class<E> type) {
        return Collections.emptySet();
    }

    boolean outOfScope(Entity source, Entity target);

    /**
     * Update interest area of the entity, return entities that are out of view, or are visible now.
     * @param entity entity to update.
     * @return newly visible or out of view entities.
     * @throws NullPointerException if entity null.
     */
    Set<Entity> update(Entity entity);

    void remove(Entity entity);
}

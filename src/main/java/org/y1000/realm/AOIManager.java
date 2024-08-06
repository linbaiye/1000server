package org.y1000.realm;

import org.y1000.entities.Entity;

import java.util.Set;

interface AOIManager {
    /**
     * Add entity and return visible entities.
     * @param entity entity to add.
     * @return visible entities.
     * @throws NullPointerException if entity null.
     */
    Set<Entity> add(Entity entity);

    boolean contains(Entity entity);

    <E extends Entity> Set<E> filterVisibleEntities(Entity entity, Class<E> type);

    boolean outOfScope(Entity source, Entity target);

    /**
     * Update interest area of the entity, return entities that are out of sight, or are insight now.
     * @param entity entity to update.
     * @return newly visible or out of sight entities.
     * @throws NullPointerException if entity null.
     */
    Set<Entity> update(Entity entity);

    void remove(Entity entity);
}

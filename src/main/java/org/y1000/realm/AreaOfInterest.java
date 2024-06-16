package org.y1000.realm;

import org.y1000.entities.PhysicalEntity;

import java.util.Set;

public interface AreaOfInterest {
    Set<PhysicalEntity> update(PhysicalEntity entity);

    boolean outOfScope(PhysicalEntity source, PhysicalEntity target);

    Set<PhysicalEntity> add(PhysicalEntity entity);

    Set<PhysicalEntity> getAllEntities();

    <E> Set<E> filterVisibleEntities(PhysicalEntity entity, Class<E> type);

    /**
     * Remove entity and return all entities seeing it before removing.
     * @param entity entity to remove
     * @return all entities seeing it.
     */
    Set<PhysicalEntity> remove(PhysicalEntity entity);
}

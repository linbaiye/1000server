package org.y1000.entities.managers;

import org.y1000.entities.PhysicalEntity;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractPhysicalEntityManager<T extends PhysicalEntity> implements EntityManager<T> {
    private final Map<Coordinate, T> coordinateIndex;

    public AbstractPhysicalEntityManager() {
        coordinateIndex = new HashMap<>();
    }

    void indexCoordinate(T entity) {
        entity.coordinates().forEach(c -> coordinateIndex.put(c, entity));
    }

    @Override
    public T findOne(Coordinate coordinate) {
        return coordinateIndex.get(coordinate);
    }
}

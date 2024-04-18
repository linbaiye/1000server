package org.y1000.entities.managers;

import org.y1000.entities.Entity;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEntityManager<E extends Entity> implements EntityManager<E> {
    private final Map<Coordinate, E> coordinateIndex;

    public AbstractEntityManager() {
        coordinateIndex = new HashMap<>();
    }

    protected void indexCoordinate(E entity) {
        //entity.coordinates().forEach(c -> coordinateIndex.put(c, entity));
    }

    @Override
    public E findOne(Coordinate coordinate) {
        return coordinateIndex.get(coordinate);
    }

}

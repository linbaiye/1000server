package org.y1000.entities.creatures;

import org.y1000.entities.Entity;
import org.y1000.entities.managers.EntityManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractEntityManager<E extends Entity> implements EntityManager<Entity> {

    private final Map<E, Set<Entity>> relevantCreatures;

    private final Set<E> entities;


    protected AbstractEntityManager() {
        this.relevantCreatures = new HashMap<>();
        entities = new HashSet<>();
    }


    protected Set<Entity> updateRelevantCreatures(E e) {
        return relevantCreatures.get(e);
    }

    public void add(E e) {
        if (entities.contains(e)) {
            return;
        }
        entities.add(e);
        relevantCreatures.put(e, new HashSet<>());
    }
}

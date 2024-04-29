package org.y1000.realm;

import org.y1000.entities.Entity;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class RelevantScopeManager {
    private final Map<Entity, RelevantScope> relevantScopeMap;

    public RelevantScopeManager() {
        relevantScopeMap = new HashMap<>(120);
    }

    private Set<Entity> mutualAdd(RelevantScope sourceScope) {
        Set<Entity> entities = new HashSet<>();
        for (Entity entity : relevantScopeMap.keySet()) {
            if (sourceScope.addIfVisible(entity)) {
                relevantScopeMap.get(entity).addIfVisible(sourceScope.source());
                entities.add(entity);
            }
        }
        return entities;
    }

    public Set<Entity> add(Entity entity) {
        Objects.requireNonNull(entity);
        if (relevantScopeMap.containsKey(entity)) {
            return Collections.emptySet();
        }
        RelevantScope scope = new RelevantScope(entity);
        relevantScopeMap.put(entity, scope);
        return mutualAdd(scope);
    }

    public Set<Entity> getAllEntities() {
        return relevantScopeMap.keySet();
    }

    public boolean contains(Entity entity) {
        return relevantScopeMap.containsKey(entity);
    }


    public <E extends Entity> Set<E> filterVisibleEntities(Entity entity, Class<E> type) {
        RelevantScope relevantScope = relevantScopeMap.get(entity);
        if (relevantScope == null) {
            return Collections.emptySet();
        }
        return relevantScope.filter(type);
    }



    public boolean outOfScope(Entity source, Entity target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return relevantScopeMap.containsKey(source) &&
                relevantScopeMap.get(source).outOfScope(target);
    }

    public Set<Entity> update(Entity entity) {
        Objects.requireNonNull(entity);
        RelevantScope relevantScope = relevantScopeMap.get(entity);
        if (relevantScope == null) {
            return Collections.emptySet();
        }
        Set<Entity> affectedEntities = new HashSet<>(relevantScope.update());
        affectedEntities.stream().map(relevantScopeMap::get)
                .filter(Objects::nonNull)
                .forEach(scp -> scp.removeIfNotVisible(entity));
        Set<Entity> added = new HashSet<>(mutualAdd(relevantScope));
        affectedEntities.addAll(added);
        return affectedEntities;
    }


    public Set<Entity> remove(Entity entity) {
        Objects.requireNonNull(entity);
        RelevantScope removed = relevantScopeMap.remove(entity);
        Set<Entity> entities = removed.filter(Entity.class);
        entities.stream().map(relevantScopeMap::get)
                .filter(Objects::nonNull)
                .forEach(scp -> scp.remove(entity));
        return entities;
    }
}

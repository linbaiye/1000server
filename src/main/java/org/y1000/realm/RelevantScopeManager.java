package org.y1000.realm;

import org.y1000.entities.PhysicalEntity;

import java.util.*;

final class RelevantScopeManager {
    private final Map<PhysicalEntity, RelevantScope> relevantScopeMap;

    public RelevantScopeManager() {
        relevantScopeMap = new HashMap<>(120);
    }

    private Set<PhysicalEntity> mutualAdd(RelevantScope sourceScope) {
        Set<PhysicalEntity> entities = new HashSet<>();
        for (PhysicalEntity entity : relevantScopeMap.keySet()) {
            if (sourceScope.addIfVisible(entity)) {
                relevantScopeMap.get(entity).addIfVisible(sourceScope.source());
                entities.add(entity);
            }
        }
        return entities;
    }

    public Set<PhysicalEntity> add(PhysicalEntity entity) {
        Objects.requireNonNull(entity);
        if (relevantScopeMap.containsKey(entity)) {
            return Collections.emptySet();
        }
        RelevantScope scope = new RelevantScope(entity);
        relevantScopeMap.put(entity, scope);
        return mutualAdd(scope);
    }

    public Set<PhysicalEntity> getAllEntities() {
        return relevantScopeMap.keySet();
    }


    public <E extends PhysicalEntity> Set<E> filterVisibleEntities(PhysicalEntity entity, Class<E> type) {
        RelevantScope relevantScope = relevantScopeMap.get(entity);
        if (relevantScope == null) {
            return Collections.emptySet();
        }
        return relevantScope.filter(type);
    }


    public boolean outOfScope(PhysicalEntity source, PhysicalEntity target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return relevantScopeMap.containsKey(source) &&
                relevantScopeMap.get(source).outOfScope(target);
    }

    public Set<PhysicalEntity> update(PhysicalEntity entity) {
        Objects.requireNonNull(entity);
        RelevantScope relevantScope = relevantScopeMap.get(entity);
        if (relevantScope == null) {
            return Collections.emptySet();
        }
        Set<PhysicalEntity> affectedEntities = new HashSet<>(relevantScope.update());
        affectedEntities.stream().map(relevantScopeMap::get)
                .filter(Objects::nonNull)
                .forEach(scp -> scp.removeIfNotVisible(entity));
        Set<PhysicalEntity> added = new HashSet<>(mutualAdd(relevantScope));
        affectedEntities.addAll(added);
        return affectedEntities;
    }


    public Set<PhysicalEntity> remove(PhysicalEntity entity) {
        Objects.requireNonNull(entity);
        RelevantScope removed = relevantScopeMap.remove(entity);
        if (removed == null)  {
            return Collections.emptySet();
        }
        Set<PhysicalEntity> entities = removed.filter(PhysicalEntity.class);
        entities.stream().map(relevantScopeMap::get)
                .filter(Objects::nonNull)
                .forEach(scp -> scp.remove(entity));
        return entities;
    }
}

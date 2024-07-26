package org.y1000.realm;

import org.y1000.entities.Entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

final class RelevantScope {
    private final Entity source;

    private final Set<Entity> entities;


    public RelevantScope(Entity source) {
        this.source = source;
        entities = new HashSet<>();
    }


    public Entity source() {
        return source;
    }


    public boolean outOfScope(Entity entity) {
        return !entity.canBeSeenAt(source.coordinate());
    }

    public boolean addIfVisible(Entity another) {
        if (another == null ||
                source.equals(another) || entities.contains(another)) {
            return false;
        }
        if (!outOfScope(another)) {
            entities.add(another);
            return true;
        }
        return false;
    }

    public <E extends Entity> Set<E> filter(Class<E> type) {
        Objects.requireNonNull(type);
        return entities.stream()
                .filter(entity -> type.isAssignableFrom(entity.getClass()))
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    public Set<Entity> update() {
        Set<Entity> result = entities.stream().filter(this::outOfScope)
                .collect(Collectors.toSet());
        entities.removeAll(result);
        return result;
    }

    public void remove(Entity another) {
        Objects.requireNonNull(another);
        entities.remove(another);
    }

    public boolean removeIfNotVisible(Entity another) {
        Objects.requireNonNull(another);
        return outOfScope(another) && entities.remove(another);
    }
}

package org.y1000.realm;

import org.y1000.entities.PhysicalEntity;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

final class RelevantScope {
    private final PhysicalEntity source;

    private final Set<PhysicalEntity> entities;


    public RelevantScope(PhysicalEntity source) {
        this.source = source;
        entities = new HashSet<>();
    }


    public PhysicalEntity source() {
        return source;
    }


    public boolean outOfScope(PhysicalEntity entity) {
        return !entity.canBeSeenAt(source.coordinate());
    }

    public boolean addIfVisible(PhysicalEntity another) {
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

    public <E extends PhysicalEntity> Set<E> filter(Class<E> type) {
        Objects.requireNonNull(type);
        return entities.stream()
                .filter(entity -> type.isAssignableFrom(entity.getClass()))
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    public Set<PhysicalEntity> update() {
        Set<PhysicalEntity> result = entities.stream().filter(this::outOfScope)
                .collect(Collectors.toSet());
        entities.removeAll(result);
        return result;
    }

    public void remove(PhysicalEntity another) {
        Objects.requireNonNull(another);
        entities.remove(another);
    }

    public boolean removeIfNotVisible(PhysicalEntity another) {
        Objects.requireNonNull(another);
        return outOfScope(another) && entities.remove(another);
    }
}

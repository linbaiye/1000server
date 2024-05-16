package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

final class RelevantScope {
    private final Entity source;

    private final Set<Entity> entities;

    private Coordinate start;

    private Coordinate end;

    public static final int X_RANGE = 15;

    public static final int Y_RANGE = 15;

    private final int xrange;

    private final int yrange;

    public RelevantScope(Entity source) {
        this(source, X_RANGE, Y_RANGE);
    }

    public RelevantScope(Entity source, int xrange, int yrange) {
        Objects.requireNonNull(source);
        this.source = source;
        entities = new HashSet<>();
        this.xrange = xrange;
        this.yrange = yrange;
        computeStartEnd();
    }

    public Entity source() {
        return source;
    }

    private void computeStartEnd() {
        start = source.coordinate().move(-xrange, -yrange);
        end = source.coordinate().move(xrange, yrange);
    }

    public boolean outOfScope(Entity entity) {
        Objects.requireNonNull(entity);
        var c = entity.coordinate();
        return start.x() > c.x() || end.x() < c.x()
                || start.y() > c.y() || end.y() < c.y();
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
        computeStartEnd();
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

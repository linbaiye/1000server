package org.y1000.entities;

import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class RelevantScope {
    private final Entity source;

    private final Set<Entity> entities;

    private Coordinate start;

    private Coordinate end;

    private static final int X_RANGE = 15;

    private static final int Y_RANGE = 15;

    public RelevantScope(Entity source) {
        this.source = source;
        entities = new HashSet<>();
        start = source.coordinate().move(-X_RANGE, -Y_RANGE);
        end = source.coordinate().move(X_RANGE, Y_RANGE);
    }

    private boolean outOfScope(Entity entity) {
        var c = entity.coordinate();
        return start.x() > c.x() || end.x() < c.x() || start.y() > c.y() || end.y() < c.y();
    }

    public boolean addIfVisible(Entity another) {
        if (another.equals(source) || entities.contains(another)) {
            return false;
        }
        if (!outOfScope(another)) {
            entities.add(another);
            return true;
        }
        return false;
    }

    public <E extends Entity> Set<E> filter(Class<E> type) {
        return entities.stream()
                .filter(entity -> type.isAssignableFrom(entity.getClass()))
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    public Set<Entity> update() {
        start = source.coordinate().move(-X_RANGE, -Y_RANGE);
        end = source.coordinate().move(X_RANGE, Y_RANGE);
        return entities.stream().filter(this::outOfScope).collect(Collectors.toSet());
    }
}

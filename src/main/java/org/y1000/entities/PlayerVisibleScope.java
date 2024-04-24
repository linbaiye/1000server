package org.y1000.entities;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public final class PlayerVisibleScope {

    private final Player source;

    private Coordinate start;

    private Coordinate end;

    private static final int X_RANGE = 15;

    private static final int Y_RANGE = 15;

    private final Set<Entity> visibleEntities;

    public PlayerVisibleScope(Player s) {
        this.source = s;
        start = s.coordinate().move(-X_RANGE, -Y_RANGE);
        end = s.coordinate().move(X_RANGE, Y_RANGE);
        visibleEntities = new HashSet<>(32);
    }

    public boolean outOfScope(Entity entity) {
        return Set.of(entity.coordinate()).stream().anyMatch(c -> start.x() > c.x() || end.x() < c.x() || start.y() > c.y() || end.y() < c.y());
    }

    public boolean withinScope(Entity entity) {
        return !outOfScope(entity);
    }


    public boolean addIfVisible(Entity entity) {
        if (outOfScope(entity) || visibleEntities.contains(entity)) {
            return false;
        }
        visibleEntities.add(entity);
        return true;
    }

    private Set<Player> filterPlayer(Set<Entity> entities) {
        return entities.stream().filter(e -> e instanceof Player).map(Player.class::cast).collect(Collectors.toSet());
    }


    public boolean contains(Entity entity) {
        return visibleEntities.contains(entity);
    }

    public void update() {
        var newStart = source.coordinate().move(-X_RANGE, -Y_RANGE);
        if (!newStart.equals(start)) {
            end = source.coordinate().move(X_RANGE, Y_RANGE);
            start = newStart;
        }
        List<Entity> outOfScopeEntities = visibleEntities.stream().filter(this::outOfScope).toList();
        outOfScopeEntities.forEach(visibleEntities::remove);
    }

    public Set<Player> getVisiblePlayers() {
        return filterPlayer(visibleEntities);
    }


    public boolean removeIfOutOfView(Entity entity) {
        return outOfScope(entity) &&
                visibleEntities.remove(entity);
    }

}

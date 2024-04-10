package org.y1000.entities.managers;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class PlayerVisibleScope {

    private final Player player;

    private Coordinate start;

    private Coordinate end;

    private static final int X_RANGE = 15;

    private static final int Y_RANGE = 15;

    private final Set<Entity> visibleEntities;

    private final Set<Entity> added;

    public PlayerVisibleScope(Player player) {
        this.player = player;
        start = player.coordinate().move(-X_RANGE, -Y_RANGE);
        end = player.coordinate().move(X_RANGE, Y_RANGE);
        visibleEntities = new HashSet<>(32);
        added = new HashSet<>();
    }

    public boolean outOfScope(Entity entity) {
        return Set.of(entity.coordinate()).stream().anyMatch(c -> start.x() > c.x() || end.x() < c.x() || start.y() > c.y() || end.y() < c.y());
    }


    public boolean addIfVisible(Entity entity) {
        if (outOfScope(entity) || visibleEntities.contains(entity)) {
            return false;
        }
        visibleEntities.add(entity);
        added.add(entity);
        return true;
    }

    private Set<Player> filterPlayer(Set<Entity> entities) {
        return entities.stream().filter(e -> e instanceof Player).map(Player.class::cast).collect(Collectors.toSet());
    }

    public Set<Player> getNewlyAddedPlayers() {
        return filterPlayer(added);
    }

    public Set<Player> getNonNewlyAddedPlayers() {
        return filterPlayer(visibleEntities).stream().filter(p -> !added.contains(p)).collect(Collectors.toSet());
    }

    public void clearNewlyAdded() {
        added.clear();
    }

    private Entity findById(long id) {
        for (Entity visibleEntity : visibleEntities) {
            if (visibleEntity.id() == id) {
                return visibleEntity;
            }
        }
        return null;
    }

    public boolean contains(Entity entity) {
        return visibleEntities.contains(entity);
    }

    public void update() {
        var newStart = player.coordinate().move(-X_RANGE, -Y_RANGE);
        if (!newStart.equals(start)) {
            end = player.coordinate().move(X_RANGE, Y_RANGE);
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

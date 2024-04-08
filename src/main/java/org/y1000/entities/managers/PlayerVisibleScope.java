package org.y1000.entities.managers;

import org.y1000.entities.GroundEntity;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PlayerVisibleScope {

    private final Player player;

    private Coordinate start;

    private Coordinate end;

    private static final int X_RANGE = 15;

    private static final int Y_RANGE = 15;

    private final Set<GroundEntity> visibleEntities;

    public PlayerVisibleScope(Player player) {
        this.player = player;
        start = player.coordinate().move(-X_RANGE, -Y_RANGE);
        end = player.coordinate().move(X_RANGE, Y_RANGE);
        visibleEntities = new HashSet<>(32);
    }

    private boolean outOfScope(GroundEntity entity) {
        return entity.coordinates().stream().anyMatch(c -> start.x() > c.x() || end.x() < c.x() || start.y() > c.y() || end.y() < c.y());
    }

    public boolean canSee(GroundEntity entity)
    {
        return !outOfScope(entity);
    }

    public void addIfVisible(GroundEntity entity)
    {
        if (outOfScope(entity) || visibleEntities.contains(entity))
        {
            return;
        }
        visibleEntities.add(entity);
    }

    public void update() {
        var newStart = player.coordinate().move(-X_RANGE, -Y_RANGE);
        if (!newStart.equals(start)) {
            end = player.coordinate().move(X_RANGE, Y_RANGE);
            start = newStart;
        }
        List<GroundEntity> outOfScopeEntities = visibleEntities.stream().filter(this::outOfScope).toList();
        outOfScopeEntities.forEach(visibleEntities::remove);
    }
}

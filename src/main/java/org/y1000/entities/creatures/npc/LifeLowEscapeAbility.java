package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.HurtAbility;
import org.y1000.util.Coordinate;

import java.util.Optional;

public class LifeLowEscapeAbility implements EscapeAbility {

    private final int lifeToEscape;

    private final int viewRange;

    public LifeLowEscapeAbility(int lifeToEscape, int viewRange) {
        Validate.isTrue(viewRange > 0);
        Validate.isTrue(lifeToEscape > 0);
        this.lifeToEscape = lifeToEscape;
        this.viewRange = viewRange;
    }

    private static Coordinate computeByDirection(Npc npc, Direction direction, int range) {
        Coordinate origin = npc.coordinate();
        Coordinate dest = origin.move(direction.x() * range, direction.y() * range);
        return npc.getRealmMap().movable(dest) ? dest : null;
    }

    private static Coordinate doCompute(Npc npc, ActiveEntity enemy, int range) {
        Direction bestDirection = enemy.coordinate().directionTo(npc.coordinate());
        Coordinate target = computeByDirection(npc, bestDirection, range);
        if (target != null) {
            return target;
        }
        int mostFarDist = 0;
        for (Direction direction: Direction.values()) {
            var tmp = computeByDirection(npc, direction, range);
            if (tmp != null) {
                var dist = tmp.directDistance(enemy.coordinate());
                if (dist > mostFarDist) {
                    mostFarDist = dist;
                    target = tmp;
                }
            }
        }
        return target;
    }

    @Override
    public Optional<Coordinate> computeSafeSpot(Npc npc, ActiveEntity enemy) {
        return Optional.ofNullable(doCompute(npc, enemy, viewRange));
    }

    public boolean shouldEscape(ActiveEntity entity) {
        return entity.findAbility(HurtAbility.class)
                .map(h -> h.currentLife() > 0 && h.currentLife() <= lifeToEscape)
                .orElse(false);
    }
}

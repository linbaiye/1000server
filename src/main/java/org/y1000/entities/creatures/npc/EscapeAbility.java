package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

import java.util.Optional;

public interface EscapeAbility {
    Optional<Coordinate> computeSafeSpot(Npc npc, ActiveEntity enemy);

    static Coordinate computeByDirection(Npc npc, Direction direction, int range) {
        Coordinate origin = npc.coordinate();
        Coordinate dest = origin.move(direction.x() * range, direction.y() * range);
        return npc.getRealmMap().movable(dest) ? dest : null;
    }

    static Coordinate doCompute(Npc npc, ActiveEntity enemy, int range) {
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

}

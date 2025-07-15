package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.util.Action;
import org.y1000.util.Coordinate;

@Slf4j
public final class AiPathUtil {
    public static Direction computeNextDirection(Npc npc,
                                                 Coordinate dest, Coordinate previous) {
        Validate.notNull(npc);
        Validate.notNull(dest);
        Validate.notNull(previous);
        var dir = npc.coordinate().directionTo(dest);
        Coordinate next = npc.coordinate().moveBy(dir);
        if (next.equals(dest)) {
            return dir != npc.direction() ? dir : null;
        }
        int minDist = Integer.MAX_VALUE;
        Direction towards = null;
        for (Direction direction : Direction.values()) {
            Coordinate coordinate = npc.coordinate().moveBy(direction);
            if (!npc.getRealmMap().movable(coordinate) || previous.equals(coordinate)) {
                continue;
            }
            int distance = coordinate.distance(dest);
            if (minDist > distance) {
                minDist = distance;
                towards = direction;
            }
        }
        return towards;
    }

    public static Direction computeNextMoveDirection(Creature npc,
                                                     Coordinate dest, Coordinate previous) {
        Validate.notNull(npc);
        Validate.notNull(dest);
        Validate.notNull(previous);
        if (dest.equals(Coordinate.Empty)) {
            return null;
        }
        var dir = npc.coordinate().directionTo(dest);
        Coordinate next = npc.coordinate().moveBy(dir);
        if (next.equals(dest) && !npc.realmMap().movable(next)) {
            // copied, but why?
            return dir != npc.direction() ? dir : null;
        }
        int minDist = Integer.MAX_VALUE;
        Direction towards = null;
        for (Direction direction : Direction.values()) {
            Coordinate coordinate = npc.coordinate().moveBy(direction);
            if (!npc.realmMap().movable(coordinate) || previous.equals(coordinate)) {
                continue;
            }
            int distance = coordinate.distance(dest);
            if (minDist > distance) {
                minDist = distance;
                towards = direction;
            }
        }
        return towards;
    }


    public static void moveProcess(Creature npc, Coordinate dest,
                                   Coordinate previous,
                                   Action noPathAction, int walkMillis, int turnMillis) {
        Direction direction = AiPathUtil.computeNextMoveDirection(npc, dest, previous);
        if (direction == null) {
            noPathAction.invoke();
            return;
        } else if (direction != npc.direction()) {
            npc.changeDirection(direction);
//            npc.stay(turnMillis);
            return;
        }
        if (npc.realmMap().movable(npc.coordinate().moveBy(direction))) {
//            npc.move(walkMillis);
        } else {
            noPathAction.invoke();
        }
    }

    public static void moveProcess(Creature npc, Coordinate dest,
                                   Coordinate previous,
                                   Action noPathAction, int walkMillis) {
        Direction direction = AiPathUtil.computeNextMoveDirection(npc, dest, previous);
        if (direction == null) {
            noPathAction.invoke();
            return;
        } else if (direction != npc.direction()) {
            npc.changeDirection(direction);
//            npc.turn();
            return;
        }
        if (npc.realmMap().movable(npc.coordinate().moveBy(direction))) {
//            npc.move(walkMillis);
        } else {
            noPathAction.invoke();
        }
    }

}

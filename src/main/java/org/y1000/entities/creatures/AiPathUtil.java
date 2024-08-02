package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Action;
import org.y1000.util.Coordinate;

public final class AiPathUtil {
    public static Direction computeNextMoveDirection(Creature creature,
                                                 Coordinate dest, Coordinate previous) {
        int minDist = Integer.MAX_VALUE;
        Direction towards = null;
        for (Direction direction : Direction.values()) {
            Coordinate coordinate = creature.coordinate().moveBy(direction);
            int distance = coordinate.distance(dest);
            if (creature.realmMap().movable(coordinate) && !previous.equals(coordinate) && minDist > distance) {
                minDist = distance;
                towards = direction;
            }
        }
        return towards;
    }


    public static void moveProcess(Npc npc, Coordinate dest,
                                   Coordinate previous,
                                   Action noPathAction, int speed) {
        Direction direction = AiPathUtil.computeNextMoveDirection(npc, dest, previous);
        if (direction == null) {
            noPathAction.invoke();
            return;
        } else if (direction != npc.direction()) {
            npc.changeDirection(direction);
            npc.emitEvent(SetPositionEvent.of(npc));
            npc.startAction(State.IDLE);
            return;
        }
        if (npc.realmMap().movable(npc.coordinate().moveBy(direction))) {
            npc.move();
        } else {
            noPathAction.invoke();
        }
    }
}

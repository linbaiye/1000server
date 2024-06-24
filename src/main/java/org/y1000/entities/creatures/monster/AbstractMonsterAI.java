package org.y1000.entities.creatures.monster;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;
import org.y1000.entities.creatures.event.MonsterMoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Action;
import org.y1000.util.Coordinate;

public abstract class AbstractMonsterAI implements MonsterAI {

    protected void changeToNewState(AbstractMonster monster, MonsterState<AbstractMonster> newState) {
        monster.changeState(newState);
        monster.emitEvent(MonsterChangeStateEvent.of(monster));
    }

    protected Direction computeNextMoveDirection(AbstractMonster monster,
                                                 Coordinate dest, Coordinate previous) {
        int minDist = Integer.MAX_VALUE;
        Direction towards = null;
        for (Direction direction : Direction.values()) {
            Coordinate coordinate = monster.coordinate().moveBy(direction);
            int distance = coordinate.distance(dest);
            if (monster.realmMap().movable(coordinate) && !previous.equals(coordinate) && minDist > distance) {
                minDist = distance;
                towards = direction;
            }
        }
        return towards;
    }

    protected void moveProcess(AbstractMonster monster, Coordinate dest,
                               Coordinate previous,
                               Action noPathAction, int speed) {
        Direction direction = computeNextMoveDirection(monster, dest, previous);
        if (direction == null) {
            noPathAction.invoke();
            return;
        } else if (direction != monster.direction()) {
            monster.changeDirection(direction);
            monster.changeState(MonsterCommonState.idle(monster));
            monster.emitEvent(SetPositionEvent.of(monster));
            return;
        }
        if (monster.realmMap().movable(monster.coordinate().moveBy(direction))) {
            monster.changeState(MonsterMoveState.move(monster, speed));
            monster.emitEvent(MonsterMoveEvent.move(monster, monster.direction(), speed));
        } else {
            noPathAction.invoke();
        }
    }

    protected void moveProcess(AbstractMonster monster, Coordinate dest,
                               Coordinate previous,
                               Action noPathAction) {
        moveProcess(monster, dest, previous,  noPathAction, monster.getStateMillis(State.WALK));
    }
}

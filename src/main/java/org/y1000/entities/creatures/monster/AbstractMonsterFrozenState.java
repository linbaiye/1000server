package org.y1000.entities.creatures.monster;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;
import org.y1000.message.MoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

@Slf4j
public abstract class AbstractMonsterFrozenState extends AbstractMonsterState {

    private final Coordinate from;

    public AbstractMonsterFrozenState(int totalMillis, Coordinate from) {
        super(totalMillis, State.FROZEN);
        this.from = from;
    }

    protected Coordinate getFrom() {
        return from;
    }

    /**
     *  What to do if the coordinate just in front of the monster not movable.
     * @param monster
     * @return state.
     */
    protected abstract MonsterState<AbstractMonster> frontNotMovable(AbstractMonster monster);

    protected abstract MonsterState<AbstractMonster> stateForTurn(AbstractMonster monster, Coordinate destination);

    protected abstract MonsterState<AbstractMonster> stateForMove(AbstractMonster monster, Coordinate destination);

    protected void tryMoveCloser(AbstractMonster monster, Coordinate destination) {
        int minDist = Integer.MAX_VALUE;
        Direction towards = null;
        for (Direction direction : Direction.values()) {
            Coordinate coordinate = monster.coordinate().moveBy(direction);
            int distance = coordinate.distance(destination);
            if (monster.realmMap().movable(coordinate) && !from.equals(coordinate) && minDist > distance) {
                minDist = distance;
                towards = direction;
            }
        }
        if (towards == null) {
            monster.changeState(frontNotMovable(monster));
            monster.emitEvent(MonsterChangeStateEvent.of(monster));
            return;
        }
        if (towards != monster.direction()) {
            monster.changeDirection(towards);
            monster.changeState(stateForTurn(monster, destination));
            monster.emitEvent(SetPositionEvent.of(monster));
            return;
        }
        if (monster.realmMap().movable(monster.coordinate().moveBy(towards))) {
            monster.changeState(stateForMove(monster, destination));
            monster.emitEvent(MoveEvent.movingTo(monster, towards));
        } else {
            monster.changeState(frontNotMovable(monster));
            monster.emitEvent(MonsterChangeStateEvent.of(monster));
        }
    }
}

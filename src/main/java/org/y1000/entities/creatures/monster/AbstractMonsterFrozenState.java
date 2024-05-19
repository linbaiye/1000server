package org.y1000.entities.creatures.monster;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.message.MoveEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractMonsterFrozenState extends AbstractMonsterState {

    private final Coordinate from;

    public AbstractMonsterFrozenState(int totalMillis, Coordinate from) {
        super(totalMillis, State.FROZEN);
        this.from = from;
    }

    protected Coordinate getFrom() {
        return from;
    }

    protected abstract MonsterState<PassiveMonster> stateForNoPath(PassiveMonster monster);

    protected abstract MonsterState<PassiveMonster> stateForTurn(PassiveMonster monster, Coordinate destination);

    protected abstract MonsterState<PassiveMonster> stateForMove(PassiveMonster monster, Coordinate destination);

    protected void tryMoveCloser(PassiveMonster monster, Coordinate destination) {
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
            monster.changeState(stateForNoPath(monster));
            monster.emitEvent(ChangeStateEvent.of(monster));
            return;
        }
        if (towards != monster.direction()) {
            monster.changeDirection(towards);
            monster.changeState(stateForTurn(monster, destination));
            monster.emitEvent(SetPositionEvent.of(monster));
        } else {
            monster.changeState(stateForMove(monster, destination));
            monster.emitEvent(MoveEvent.movingTo(monster, monster.direction()));
        }
    }
}

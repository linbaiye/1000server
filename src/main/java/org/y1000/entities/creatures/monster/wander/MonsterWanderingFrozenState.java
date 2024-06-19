package org.y1000.entities.creatures.monster.wander;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterFrozenState;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.util.Coordinate;

@Slf4j
public final class MonsterWanderingFrozenState extends AbstractMonsterFrozenState implements WanderingState {

    private final Coordinate destination;

    public MonsterWanderingFrozenState(int totalMillis, Coordinate destination, Coordinate from) {
        super(totalMillis, from);
        this.destination = destination;
    }

    @Override
    protected void nextMove(AbstractMonster monster) {
        if (!monster.realmMap().movable(destination) || destination.equals(monster.coordinate())) {
            monster.changeState(MonsterWanderingIdleState.reroll(monster));
            monster.emitEvent(MonsterChangeStateEvent.of(monster));
        } else {
            tryMoveCloser(monster, destination);
        }
    }


    public static MonsterWanderingFrozenState Freeze(AbstractMonster monster, Coordinate dest, Coordinate from) {
        return new MonsterWanderingFrozenState(monster.getStateMillis(State.FROZEN), dest, from);
    }

    @Override
    protected MonsterState<AbstractMonster> frontNotMovable(AbstractMonster monster) {
        return MonsterWanderingIdleState.reroll(monster);
    }

    @Override
    protected MonsterState<AbstractMonster> stateForTurn(AbstractMonster monster, Coordinate destination) {
        return MonsterWanderingIdleState.again(monster, destination, getFrom());
    }

    @Override
    protected MonsterState<AbstractMonster> stateForMove(AbstractMonster monster, Coordinate destination) {
        return MonsterWanderingMoveState.move(monster, destination);
    }
}


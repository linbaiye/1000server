package org.y1000.entities.creatures.monster.wander;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterFrozenState;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.util.Coordinate;

@Slf4j
public final class MonsterWanderingFrozenState extends AbstractMonsterFrozenState implements MonsterWanderingState {

    private final Coordinate destination;

    public MonsterWanderingFrozenState(int totalMillis, Coordinate destination, Coordinate from) {
        super(totalMillis, from);
        this.destination = destination;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        if (!monster.realmMap().movable(destination) || destination.equals(monster.coordinate())) {
          //  log.debug("Destination {} not movable, restart.", destination);
            monster.changeState(MonsterWanderingIdleState.reroll(monster));
            monster.emitEvent(ChangeStateEvent.of(monster));
        } else {
            tryMoveCloser(monster, destination);
        }
    }


    public static MonsterWanderingFrozenState Freeze(PassiveMonster monster, Coordinate dest, Coordinate from) {
        return new MonsterWanderingFrozenState(monster.getStateMillis(State.FROZEN), dest, from);
    }

    @Override
    protected MonsterState<PassiveMonster> stateForNoPath(PassiveMonster monster) {
        return MonsterWanderingIdleState.reroll(monster);
    }

    @Override
    protected MonsterState<PassiveMonster> stateForTurn(PassiveMonster monster, Coordinate destination) {
        return MonsterWanderingIdleState.again(monster, destination, getFrom());
    }

    @Override
    protected MonsterState<PassiveMonster> stateForMove(PassiveMonster monster, Coordinate destination) {
        return MonsterWanderingMoveState.move(monster, destination);
    }
}


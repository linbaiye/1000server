package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.monster.AbstractMonsterFrozenState;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

public final class MonsterFightFrozenState extends AbstractMonsterFrozenState implements MonsterFightState {

    public MonsterFightFrozenState(int totalMillis, Coordinate from) {
        super(totalMillis, from);
    }

    @Override
    protected MonsterState<AbstractMonster> frontNotMovable(AbstractMonster monster) {
        return MonsterFightIdleState.start(monster);
    }

    @Override
    protected MonsterState<AbstractMonster> stateForTurn(AbstractMonster monster, Coordinate destination) {
        return MonsterFightIdleState.nextStep(monster, getFrom());
    }

    @Override
    protected MonsterState<AbstractMonster> stateForMove(AbstractMonster monster, Coordinate destination) {
        return MonsterFightMoveState.towardsCurrentDirection(monster);
    }

    @Override
    protected void nextMove(AbstractMonster monster) {
        attackIfAdjacentOrNextMove(monster, () -> tryMoveCloser(monster, monster.getFightingEntity().coordinate()));
    }

    public static MonsterFightFrozenState next(AbstractMonster monster, Coordinate from) {
        int stateMillis = monster.getStateMillis(State.FROZEN);
        return new MonsterFightFrozenState(stateMillis, from);
    }

    @Override
    public void afterHurt(AbstractMonster creature) {
        if (elapse(creature.getStateMillis(State.HURT))) {
            nextMove(creature);
        } else {
            creature.changeState(this);
        }
    }
}

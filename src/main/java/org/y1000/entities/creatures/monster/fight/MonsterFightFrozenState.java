package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterFrozenState;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

public class MonsterFightFrozenState extends AbstractMonsterFrozenState {

    public MonsterFightFrozenState(int totalMillis, Coordinate from) {
        super(totalMillis, from);
    }

    @Override
    protected MonsterState<PassiveMonster> stateForNoPath(PassiveMonster monster) {
        return MonsterFightIdleState.start(monster);
    }

    @Override
    protected MonsterState<PassiveMonster> stateForTurn(PassiveMonster monster, Coordinate destination) {
        return MonsterFightIdleState.nextStep(monster, getFrom());
    }

    @Override
    protected MonsterState<PassiveMonster> stateForMove(PassiveMonster monster, Coordinate destination) {
        return MonsterFightMoveState.towardsCurrentDirection(monster);
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        PhysicalEntity physicalEntity = monster.getFightingEntity();
        if (physicalEntity == null) {
            monster.nextHuntMove();
        } else {
            tryMoveCloser(monster, physicalEntity.coordinate());
        }
    }

    public static MonsterFightFrozenState next(PassiveMonster monster, Coordinate from) {
        int stateMillis = monster.getStateMillis(State.FROZEN);
        return new MonsterFightFrozenState(stateMillis, from);
    }
}

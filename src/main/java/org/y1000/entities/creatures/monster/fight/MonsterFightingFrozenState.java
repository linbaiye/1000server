package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterFrozenState;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.util.Coordinate;

public final class MonsterFightingFrozenState extends AbstractMonsterFrozenState implements MonsterFightingState {

    private Creature target;

    private MonsterFightingFrozenState(int totalMillis, Creature target, Coordinate from) {
        super(totalMillis, from);
        this.target = target;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        tryMoveCloser(monster, target.coordinate());
    }

    @Override
    public Creature currentTarget() {
        return target;
    }

    @Override
    public void afterHurt(PassiveMonster monster, Creature attacker) {
        if (target.coordinate().distance(monster.coordinate()) > 1) {
            target = attacker;
            monster.changeState(this);
        } else {
            monster.changeState(new MonsterCooldownState(monster.cooldown(), target));
        }
    }


    @Override
    protected MonsterState<PassiveMonster> stateForNoPath(PassiveMonster monster) {
        return MonsterFightingIdleState.hunt(monster, target);
    }

    @Override
    protected MonsterState<PassiveMonster> stateForTurn(PassiveMonster monster, Coordinate destination) {
        return MonsterFightingIdleState.continueHunting(monster, target, getFrom());
    }

    @Override
    protected MonsterState<PassiveMonster> stateForMove(PassiveMonster monster, Coordinate destination) {
        return MonsterFightingMoveState.move(monster, target);
    }

    public static MonsterFightingFrozenState freeze(PassiveMonster monster, Creature target, Coordinate from) {
        return new MonsterFightingFrozenState(monster.getStateMillis(State.FROZEN), target, from);
    }
}

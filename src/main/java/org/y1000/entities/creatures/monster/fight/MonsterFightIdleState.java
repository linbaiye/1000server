package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

public final class MonsterFightIdleState extends AbstractMonsterIdleState implements MonsterFightState {

    public MonsterFightIdleState(int totalMillis, Coordinate from) {
        super(totalMillis, from);
    }

    @Override
    protected void nextMove(AbstractMonster monster) {
        attackIfAdjacentOrNextMove(monster, () -> {
            monster.changeState(MonsterFightFrozenState.next(monster, getFrom()));
            monster.emitEvent(CreatureChangeStateEvent.of(monster));
        });
    }

    public static MonsterFightIdleState start(AbstractMonster monster) {
        return new MonsterFightIdleState(monster.getStateMillis(State.IDLE), new Coordinate(0, 0));
    }

    public static MonsterFightIdleState nextStep(AbstractMonster monster, Coordinate from) {
        return new MonsterFightIdleState(monster.getStateMillis(State.IDLE), from);
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

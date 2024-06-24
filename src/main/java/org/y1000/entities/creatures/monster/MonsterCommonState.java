package org.y1000.entities.creatures.monster;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.MonsterChangeStateEvent;

import java.util.Set;

@Slf4j
public final class MonsterCommonState extends AbstractMonsterState {
    private static final Set<State> ACCEPTABLE_STATES = Set.of(State.IDLE, State.FROZEN, State.ATTACK);

    private MonsterCommonState(int totalMillis, State stat) {
        super(totalMillis, stat);
        Validate.isTrue(ACCEPTABLE_STATES.contains(stat));
    }
    @Override
    protected void nextMove(AbstractMonster monster) {
        switch (stateEnum()) {
            case IDLE -> monster.AI().onIdleDone(monster);
            case FROZEN -> monster.AI().onFrozenDone(monster);
            case ATTACK -> monster.AI().onAttackDone(monster);
        }
    }

    @Override
    public void afterHurt(AbstractMonster creature) {
        if (stateEnum() == State.ATTACK || elapse(creature.getStateMillis(State.HURT))) {
            nextMove(creature);
        } else {
            creature.changeState(this);
            creature.emitEvent(MonsterChangeStateEvent.of(creature));
        }
    }
    public static MonsterCommonState idle(AbstractMonster monster) {
        return new MonsterCommonState(monster.getStateMillis(State.IDLE), State.IDLE);
    }

    public static MonsterCommonState cooldown(AbstractMonster monster) {
        return new MonsterCommonState(monster.cooldown(), State.IDLE);
    }

    public static MonsterCommonState freeze(AbstractMonster monster) {
        return new MonsterCommonState(monster.getStateMillis(State.FROZEN), State.FROZEN);
    }

    public static MonsterCommonState attack(AbstractMonster monster) {
        return new MonsterCommonState(monster.getStateMillis(State.ATTACK), State.ATTACK);
    }
}

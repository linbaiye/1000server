package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.util.Coordinate;

public final class MonsterFightingIdleState extends AbstractMonsterIdleState implements MonsterFightingState {

    private Creature target;

    public MonsterFightingIdleState(int totalMillis, Creature target, Coordinate from) {
        super(totalMillis, from);
        this.target = target;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.changeState(MonsterFightingFrozenState.freeze(monster, target, getFrom()));
        monster.emitEvent(CreatureChangeStateEvent.of(monster));
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


    public static MonsterFightingIdleState hunt(PassiveMonster monster, Creature attacker) {
        return new MonsterFightingIdleState(monster.getStateMillis(State.IDLE), attacker, new Coordinate(0, 0));
    }

    public static MonsterFightingIdleState continueHunting(PassiveMonster monster, Creature attacker, Coordinate from) {
        return new MonsterFightingIdleState(monster.getStateMillis(State.IDLE), attacker, from);
    }
}

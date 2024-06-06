package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;

public class MonsterFightCooldownState extends AbstractMonsterState {

    public MonsterFightCooldownState(int totalMillis) {
        super(totalMillis, State.IDLE);
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.nextHuntMove();
    }

    public static MonsterFightCooldownState cooldown(int cooldown) {
        return new MonsterFightCooldownState(cooldown);
    }
}

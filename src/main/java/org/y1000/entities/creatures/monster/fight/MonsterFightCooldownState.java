package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.monster.AbstractMonsterState;

public class MonsterFightCooldownState extends AbstractMonsterState {

    public MonsterFightCooldownState(int totalMillis) {
        super(totalMillis, State.IDLE);
    }

    @Override
    protected void nextMove(AbstractMonster monster) {
        monster.fight();
    }

    public static MonsterFightCooldownState cooldown(int cooldown) {
        return new MonsterFightCooldownState(cooldown);
    }

    @Override
    public void afterHurt(AbstractMonster creature) {
        creature.fight();
    }
}

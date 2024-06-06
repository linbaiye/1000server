package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.AbstractCreatureHurtState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.monster.fight.MonsterFightCooldownState;

public final class MonsterHurtState extends AbstractCreatureHurtState<PassiveMonster> {

    public MonsterHurtState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    protected void recovery(PassiveMonster monster) {
        monster.changeState(MonsterFightCooldownState.cooldown(monster.cooldown()));
    }
}

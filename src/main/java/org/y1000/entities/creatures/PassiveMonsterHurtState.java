package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PassiveMonsterHurtState extends AbstractCreatureHurtState<PassiveMonster> {

    public PassiveMonsterHurtState(Creature attacker, int totalMillis, AfterHurtAction<PassiveMonster> after) {
        super(totalMillis, after, attacker);
    }



    @Override
    public void afterAttacked(PassiveMonster monster, Creature attacker) {
        monster.changeState(new PassiveMonsterHurtState(getAttacker(), monster.getStateMillis(State.HURT), getAction()));
    }
}

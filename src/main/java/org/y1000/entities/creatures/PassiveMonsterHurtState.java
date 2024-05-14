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

    /*protected void nextMove(PassiveMonster monster) {
        if (monster.coordinate().distance(attacker.coordinate()) <= 1)  {
            monster.changeDirection(monster.coordinate().computeDirection(attacker.coordinate()));
            if (monster.recoveryCooldown() > 0 || monster.attackCooldown() > 0) {
                monster.changeState(MonsterCooldownState.of(monster, attacker));
                monster.emitEvent(SetPositionEvent.fromCreature(monster));
            } else {
                monster.changeState(MonsterAttackState.attack(monster, attacker));
                monster.emitEvent(CreatureAttackEvent.ofMonster(monster));
            }
        } else {
            // need to move to attacker.
            monster.changeState(PassiveMonsterIdleState.ofMonster(monster));
            monster.emitEvent(SetPositionEvent.fromCreature(monster));
        }
    }*/
}

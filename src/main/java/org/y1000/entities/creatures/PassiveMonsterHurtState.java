package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.message.SetPositionEvent;

@Slf4j
public final class PassiveMonsterHurtState extends AbstractMonsterState {

    private final Creature attacker;

    public PassiveMonsterHurtState(Creature attacker, int totalMillis) {
        super(totalMillis, State.HURT);
        this.attacker = attacker;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
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
    }

    public static PassiveMonsterHurtState attacked(PassiveMonster monster, Creature attacker) {
        return new PassiveMonsterHurtState(attacker, monster.getStateMillis(State.HURT));
    }
}

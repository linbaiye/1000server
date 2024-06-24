package org.y1000.entities.creatures.monster;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.CreatureSoundEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Action;

public abstract class AbstractMonsterFightAI extends AbstractMonsterAI {


    protected void attackProcess(AbstractMonster monster, Action doAttackAction) {
        Direction towards = monster.coordinate().computeDirection(monster.getFightingEntity().coordinate());
        if (towards != monster.direction()) {
            monster.changeDirection(towards);
            monster.emitEvent(SetPositionEvent.of(monster));
        }
        if (monster.cooldown() > 0) {
            changeToNewState(monster, MonsterCommonState.cooldown(monster));
            return;
        }
        doAttackAction.invoke();
    }

    protected void doMeleeAttack(AbstractMonster monster) {
        monster.cooldownAttack();
        monster.getFightingEntity().attackedBy(monster);
        monster.changeState(MonsterCommonState.attack(monster));
        monster.emitEvent(CreatureAttackEvent.ofMonster(monster));
        monster.attackSound().ifPresent(s -> monster.emitEvent(new CreatureSoundEvent(monster, s)));
    }

    @Override
    public void onHurtDone(AbstractMonster monster) {
        if (monster.state() instanceof MonsterHurtState hurtState) {
            hurtState.previousState().afterHurt(monster);
        } else {
            throw new IllegalStateException("Not called from hurt state.");
        }
    }
}

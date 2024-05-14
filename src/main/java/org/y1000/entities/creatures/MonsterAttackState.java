package org.y1000.entities.creatures;

import org.y1000.message.SetPositionEvent;

public final class MonsterAttackState extends AbstractCreatureAttackState<PassiveMonster> {

    private final Creature target;

    public MonsterAttackState(int lengthMillis, Creature target) {
        super(lengthMillis);
        this.target = target;
    }

    @Override
    public void update(PassiveMonster monster, int delta) {
        if (elapsedMillis() == 0) {
            monster.cooldownAttack();
            target.attackedBy(monster);
        }
        if (elapse(delta)) {
            monster.changeState(MonsterCooldownState.of(monster, target));
            monster.emitEvent(SetPositionEvent.ofCreature(monster));
        }
    }

    public static MonsterAttackState attack(PassiveMonster monster, Creature target) {
        return new MonsterAttackState(monster.getStateMillis(State.ATTACK), target);
    }
}

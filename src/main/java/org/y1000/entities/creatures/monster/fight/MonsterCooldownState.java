package org.y1000.entities.creatures.monster.fight;


import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;

@Slf4j
public final class MonsterCooldownState extends AbstractMonsterState implements MonsterFightingState {

    private final Creature currentAttacker;

    public MonsterCooldownState(int totalMillis,
                                Creature currentAttacker) {
        super(totalMillis, State.IDLE);
        this.currentAttacker = currentAttacker;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.attack(currentAttacker);
    }

    @Override
    public void afterHurt(PassiveMonster passiveMonster, Creature attacker) {
        passiveMonster.attack(attacker);
    }
}

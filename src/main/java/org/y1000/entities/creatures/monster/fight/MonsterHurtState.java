package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.AbstractCreatureHurtState;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;

public final class MonsterHurtState extends AbstractCreatureHurtState<AbstractMonster> implements MonsterState<AbstractMonster> {

    /**
     * The state before getting hurt.
     */
    private final MonsterState<AbstractMonster> previousState;

    public MonsterHurtState(int totalMillis, MonsterState<AbstractMonster> previousState) {
        super(totalMillis);
        this.previousState = previousState;
    }

    @Override
    protected void recovery(AbstractMonster monster) {
        previousState.afterHurt(monster);
    }

    @Override
    public void afterHurt(AbstractMonster creature) {
        previousState.afterHurt(creature);
    }
}

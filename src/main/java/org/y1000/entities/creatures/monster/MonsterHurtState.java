package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.AbstractCreatureHurtState;

public final class MonsterHurtState extends AbstractCreatureHurtState<AbstractMonster> implements MonsterState<AbstractMonster> {

    /**
     * The state before getting hurt.
     */
    private final MonsterState<AbstractMonster> previousState;

    public MonsterHurtState(int totalMillis,
                            MonsterState<AbstractMonster> previousState) {
        super(totalMillis);
        if (previousState instanceof MonsterHurtState hurtState)
            this.previousState = hurtState.previousState;
       else
            this.previousState = previousState;
    }

    @Override
    protected void recovery(AbstractMonster monster) {
        monster.AI().onHurtDone(monster);
    }

    public MonsterState<AbstractMonster> previousState() {
        return previousState;
    }

    @Override
    public void afterHurt(AbstractMonster creature) {
        creature.AI().onHurtDone(creature);
    }
}

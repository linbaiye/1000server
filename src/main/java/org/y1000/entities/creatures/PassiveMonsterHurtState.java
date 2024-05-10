package org.y1000.entities.creatures;

import org.y1000.realm.RealmImpl;

public final class PassiveMonsterHurtState extends AbstractCreatureHurtState<PassiveMonster> {

    private final Creature attacker;

    private final int total;

    public PassiveMonsterHurtState(Creature attacker, int recovery) {
        this.attacker = attacker;
        total = recovery * RealmImpl.STEP_MILLIS;
    }

    public Creature attacker() {
        return attacker;
    }

    @Override
    public void update(PassiveMonster monster, int delta) {
        elapse(delta);
        if (elapsedMillis() >= total) {
            monster.AI().nextMove();
        }
    }
}

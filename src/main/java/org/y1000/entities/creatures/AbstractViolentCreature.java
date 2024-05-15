package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.realm.RealmImpl;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractViolentCreature<C extends AbstractViolentCreature<C, S>, S extends CreatureState<C>> extends AbstractCreature<C, S>
        implements ViolentCreature {

    private int recoveryCooldown;

    private int attackCooldown;

    protected AbstractViolentCreature(long id, Coordinate coordinate, Direction direction, String name, Map<State, Integer> stateMillis) {
        super(id, coordinate, direction, name, stateMillis);
    }

    protected boolean randomAvoidance(int attackerHit) {
        var rand = ThreadLocalRandom.current().nextInt(attackerHit + 75 + avoidance());
        return rand < avoidance();
    }

    @Override
    public int recoveryCooldown() {
        return recoveryCooldown;
    }

    @Override
    public int attackCooldown() {
        return attackCooldown;
    }

    protected void cooldown(int delta) {
        recoveryCooldown = recoveryCooldown > delta ? recoveryCooldown - delta : 0;
        attackCooldown = attackCooldown > delta ? attackCooldown - delta : 0;
    }

    protected abstract S createHurtState(ViolentCreature attacker);

    @Override
    public void attackedBy(ViolentCreature attacker) {
        if (!state().attackable() || randomAvoidance(attacker.hit())) {
            return;
        }
        cooldownRecovery();
        changeState(createHurtState(attacker));
        emitEvent(new CreatureHurtEvent(this));
    }

    protected void cooldownRecovery() {
        recoveryCooldown = recovery() * RealmImpl.STEP_MILLIS;
    }

    protected void cooldownAttack() {
        attackCooldown = attackSpeed() * RealmImpl.STEP_MILLIS;
    }
}


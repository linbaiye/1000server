package org.y1000.entities.creatures;

import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public abstract class AbstractViolentCreature<C extends AbstractViolentCreature<C, S>, S extends CreatureState<C>>
        extends AbstractCreature<C, S> implements ViolentCreature {

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

    protected abstract Logger log();

    protected void cooldown(int delta) {
        recoveryCooldown = recoveryCooldown > delta ? recoveryCooldown - delta : 0;
        attackCooldown = attackCooldown > delta ? attackCooldown - delta : 0;
    }

    protected boolean handleAttacked(C creature, int hit, Function<State, S> hurtStateSupplier) {
        if (!state().attackable() || randomAvoidance(hit)) {
            return false;
        }
        cooldownRecovery();
        state().moveToHurtCoordinate(creature);
        State afterHurtState = state().decideAfterHurtState();
        changeState(hurtStateSupplier.apply(afterHurtState));
        emitEvent(new CreatureHurtEvent(this, afterHurtState));
        return true;
    }

    public void cooldownRecovery() {
        recoveryCooldown = recovery() * Realm.STEP_MILLIS;
    }

    public void cooldownAttack() {
        attackCooldown = attackSpeed() * Realm.STEP_MILLIS;
    }
}


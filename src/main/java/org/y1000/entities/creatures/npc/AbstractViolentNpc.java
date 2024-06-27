package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.slf4j.Logger;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.event.EntityEventListener;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractViolentNpc<C extends Creature, S extends CreatureState<C>>
        extends AbstractNpc<C, S> implements ViolentCreature, EntityEventListener {
    private int recoveryCooldown;

    private int attackCooldown;

    private final Damage damage;

    @Getter
    private AttackableEntity fightingEntity;

    public AbstractViolentNpc(long id, Coordinate coordinate, Direction direction, String name,
                              Map<State, Integer> stateMillis, AttributeProvider attributeProvider,
                              RealmMap realmMap) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap);
        this.damage = new Damage(attributeProvider.damage(), 0, 0, 0);
    }

    public Optional<String> attackSound() {
        return attributeProvider().attackSound();
    }

    @Override
    public int recoveryCooldown() {
        return recoveryCooldown;
    }

    @Override
    public Damage damage() {
        return damage;
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

    public void cooldownRecovery() {
        recoveryCooldown = recovery() * Realm.STEP_MILLIS;
    }

    public void cooldownAttack() {
        attackCooldown = attackSpeed() * Realm.STEP_MILLIS;
    }

    public void setFightingEntity(AttackableEntity entity){
        Objects.requireNonNull(entity, "entity can't be null");
        if (this.fightingEntity != null) {
            this.fightingEntity.deregisterEventListener(this);
        }
        this.fightingEntity = entity;
        this.fightingEntity.registerEventListener(this);
    }


    @Override
    public int attackSpeed() {
        return attributeProvider().attackSpeed();
    }

    @Override
    public int recovery() {
        return attributeProvider().recovery();
    }

    @Override
    public int hit() {
        return attributeProvider().hit();
    }

    protected void clearFightingEntity() {
        if (this.fightingEntity != null) {
            this.fightingEntity.deregisterEventListener(this);
            this.fightingEntity = null;
        }
    }
}

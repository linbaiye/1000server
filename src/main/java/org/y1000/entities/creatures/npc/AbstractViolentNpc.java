package org.y1000.entities.creatures.npc;

import org.slf4j.Logger;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.event.NpcChangeStateEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractViolentNpc
        extends AbstractNpc implements ViolentNpc {
    private int recoveryCooldown;

    private int attackCooldown;

    private final Damage damage;

    private NpcAI ai;

    private final NpcRangedSkill skill;

    public AbstractViolentNpc(long id, Coordinate coordinate, Direction direction, String name,
                              Map<State, Integer> stateMillis, AttributeProvider attributeProvider,
                              RealmMap realmMap, NpcAI ai, NpcRangedSkill skill) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap);
        this.damage = new Damage(attributeProvider.damage(), 0, 0, 0);
        this.ai = ai;
        this.skill = skill;
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
    public Optional<NpcRangedSkill> skill() {
        return Optional.ofNullable(skill);
    }

    @Override
    public void changeAI(NpcAI newAI) {
        this.ai = newAI;
        this.ai.start(this);
    }

    @Override
    public void start() {
        this.ai.start(this);
    }

    @Override
    public void onActionDone() {
        if (stateEnum() == State.DIE) {
            changeAI(NpcFrozenAI.INSTANCE);
        }
        handleActionDone(() -> ai.onActionDone(this));
    }

    @Override
    public void onMoveFailed() {
        this.ai.onMoveFailed(this);
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

    @Override
    public void startAttackAction(boolean withSound) {
        doAttackAction(NpcCommonState.attack(getStateMillis(State.ATTACK)));
        if (withSound) {
            attackSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        }
    }

    private void doAttackAction(NpcState attackState) {
        cooldownAttack();
        changeState(attackState);
        emitEvent(new CreatureAttackEvent(this));
    }

    public void startRangedAttack(AttackableActiveEntity target) {
        if (skill == null) {
            throw new IllegalStateException("ranged attack is not supported.");
        }
        if (target == null || !skill.isAvailable()) {
            return;
        }
        doAttackAction(new NpcRangedAttackState(getStateMillis(State.ATTACK), skill.getSwingSound(), skill.getProjectileSpriteId(), target, this));
    }

    @Override
    public void startAction(State state) {
        if (state == State.ATTACK) {
            startAttackAction(true);
        } else if (state == State.COOLDOWN) {
            changeState(NpcCommonState.idle(cooldown()));
            emitEvent(NpcChangeStateEvent.of(this));
        } else {
            super.startAction(state);
        }
    }

    protected void doUpdate(int delta) {
        cooldown(delta);
        state().update(this, delta);
        skill().ifPresent(s -> s.cooldown(delta));
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
}

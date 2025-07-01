package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.monster.NpcStateEnum;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.event.NpcChangeStateEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractViolentNpc
        extends AbstractNpc implements ViolentNpc {
    private int recoveryCooldown;

    private int attackCooldown;

    private final Damage damage;

    private final NpcRangedSkill skill;

    public AbstractViolentNpc(long id, Coordinate coordinate, Direction direction, String name,
                              Map<NpcStateEnum, Integer> stateMillis, AttributeProvider attributeProvider,
                              RealmMap realmMap, NpcAI ai, NpcRangedSkill skill, List<NpcSpell> spellList) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, spellList, ai);
        this.damage = new Damage(attributeProvider.damage(), 0, 0, 0);
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
    public int attackCooldown() {
        return attackCooldown;
    }

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
        doAttackAction(NpcCommonState.attack(this));
        if (withSound) {
            attackSound().ifPresent(s -> emitEvent(new EntitySoundEvent(this, s)));
        }
    }

    @Override
    void hurt(ViolentCreature attacker) {
        cooldownRecovery();
        doHurtAction(attacker, recoveryCooldown());
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
//        doAttackAction(new NpcRangedAttackState(getStateMillis(NpcStateEnum.Attack), skill.getSwingSound(), skill.getProjectileSpriteId(), target, this));
    }

    @Override
    public void startAction(NpcStateEnum npcStateEnum) {
        if (npcStateEnum == NpcStateEnum.Attack) {
            startAttackAction(true);
        } else if (npcStateEnum == NpcStateEnum.Idle) {
            changeState(NpcCommonState.idle(this, cooldown()));
            emitEvent(NpcChangeStateEvent.of(this));
        } else {
            super.startAction(npcStateEnum);
        }
    }

    @Override
    public void update(int delta) {
        cooldown(delta);
        skill().ifPresent(s -> s.cooldown(delta));
        npcState().update(delta);
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

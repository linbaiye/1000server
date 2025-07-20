package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.HurtAbility;

@Slf4j
public class CombatAI extends AbstractMovableNpcAI {
    private final ActiveEntity enemy;
    private final NpcMeleeAbility meleeAbility;
    private final NpcHurtAbility hurtAbility;
    private final HurtAbility enemyHurtAbility;

    public CombatAI(Npc npc, ActiveEntity entity,
                     NpcUpdatableAbility ability) {
        super(npc);
        this.enemy = entity;
        changeAbility(ability);
        this.hurtAbility = npc.findAbility(NpcHurtAbility.class).orElseThrow();
        this.meleeAbility = npc.findAbility(NpcMeleeAbility.class).orElseThrow();
        hurtAbility.setHurtTrigger(this::onAttacked);
        this.enemyHurtAbility = enemy.findAbility(HurtAbility.class).orElseThrow();
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }

    @Override
    public void start() {
        npc().findAbility(NpcMoveAbility.class).ifPresent(NpcMoveAbility::enableFastMove);
        tryAttack();
    }


    private void shootOrCooldown(NpcShootAbility shootAbility) {
        resetPrevious();
        if (!shootAbility.canAttack()) {
            stay(shootAbility.cooldownLeft());
        }
        else {
            changeAbility(shootAbility);
            shootAbility.shoot(npc(), enemy);
        }
    }

    private void doRangedAttack(NpcShootAbility shootAbility) {
        if (shootAbility.shouldEscape(npc(), enemy)) {
            shootAbility.computeDirectionToSafeSpot(npc(), enemy)
                    .ifPresentOrElse(this::moveOrTurn,
                        () -> shootOrCooldown(shootAbility));
        } else {
            shootOrCooldown(shootAbility);
        }
    }

    private void doMeleeAttack() {
        if (npc().coordinate().directDistance(enemy.coordinate()) >= 2) {
            moveCloser(enemy.coordinate());
            return;
        }
        Direction direction = npc().coordinate().directionTo(enemy.coordinate());
        if (direction != npc().direction()) {
            changeAbilityOrThrow(NpcTurnAbility.class)
                    .turn(npc(), direction);
            return;
        }
        if (meleeAbility.canAttack() && hurtAbility.isRecovered()) {
            changeAbility(meleeAbility).apply(npc(), enemy);
        } else {
            stay(Math.max(meleeAbility.attackCooldownLeft(), hurtAbility.recoveryLeft()));
        }
    }

    private void tryAttack() {
        if (!enemy.canBeSeenAt(npc().coordinate()) || !enemyHurtAbility.canBeAttacked()) {
            npc().startAI(new WanderingAI(npc()));
            return;
        }
        npc().findAbility(NpcShootAbility.class).ifPresentOrElse(s -> {
            if (s.hasProjectile()) {
                doRangedAttack(s);
            } else {
                doMeleeAttack();
            }
        }, this::doMeleeAttack);
    }

    private void stay(int millis) {
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc(), millis);
    }


    private void stayOrAttack(NpcMoveAbility ability) {
        if (ability.idleTime() > 0) {
            stay(ability.idleTime());
        } else {
            tryAttack();
        }
    }

    void onNonDieAbilityDone(NpcUpdatableAbility doneAbility) {
        if (doneAbility instanceof NpcMoveAbility moveAbility) {
            computePrevious();
            stayOrAttack(moveAbility);
        } else if (doneAbility instanceof NpcTurnAbility) {
            npc().findAbility(NpcMoveAbility.class).ifPresent(this::stayOrAttack);
        } else {
            tryAttack();
        }
    }

    @Override
    void onMoveFailed() {
        stay(100);
    }

    private void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        applyHurtAbility(ability);
    }
}

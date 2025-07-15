package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.HurtAbility;

@Slf4j
public class CombatAI extends AbstractMovableNpcAI {
    private final ActiveEntity enemy;
    private final NpcMeleeAbility attackAbility;
    private final NpcHurtAbility hurtAbility;
    private final HurtAbility enemyHurtAbility;

    private CombatAI(Npc npc, ActiveEntity entity,
                     NpcAbility ability) {
        super(npc);
        this.enemy = entity;
        changeAbility(ability);
        this.hurtAbility = npc.findAbility(NpcHurtAbility.class).orElseThrow();
        this.attackAbility = npc.findAbility(NpcMeleeAbility.class).orElseThrow();
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

    private void doRangedAttack(NpcShootAbility shootAbility) {
        if (shootAbility.shouldEscape(npc(), enemy)) {
            EscapeAI escapeAI = new EscapeAI(npc(), enemy, currentAbility(), shootAbility);
            npc().startAI(escapeAI);
            log.debug("change to escape.");
            return;
        }
        if (!shootAbility.canAttack()) {
            log.debug("Wait cooldown.");
            stay(shootAbility.cooldownLeft());
        }
        else {
            log.debug("Shoot");
            changeAbility(shootAbility);
            shootAbility.shoot(npc(), enemy);
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
            log.debug("Turn");
            return;
        }
        if (attackAbility.canAttack() && hurtAbility.isRecovered()) {
            log.debug("melee attack");
            changeAbility(attackAbility).apply(npc(), enemy);
        } else {
            log.debug("stay.");
            stay(Math.max(attackAbility.attackCooldownLeft(), hurtAbility.recoveryLeft()));
        }
    }

    private void tryAttack() {
        if (!enemy.canBeSeenAt(npc().coordinate()) || !enemyHurtAbility.canBeAttacked()) {
            log.debug("Back to wander.");
            npc().startAI(new WanderingAI(npc()));
            return;
        }
        npc().findAbility(NpcShootAbility.class).ifPresentOrElse(s -> {
            if (s.hasProjectile()) {
                log.debug("try ranged");
                doRangedAttack(s);
            } else {
                log.debug("try melee");
                doMeleeAttack();
            }
        }, this::doMeleeAttack);
    }

    private void stay(int millis) {
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc(), millis);
    }

    private void onMoved(NpcMoveAbility ability) {
        computePrevious();
        stayOrAttack(ability);
    }

    private void stayOrAttack(NpcMoveAbility ability) {
        if (ability.idleTime() > 0) {
            stay(ability.idleTime());
        } else {
            tryAttack();
        }
    }

    void onNonDieAbilityDone(NpcAbility doneAbility) {
        if (doneAbility instanceof NpcMoveAbility moveAbility) {
            onMoved(moveAbility);
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

    public static CombatAI hurtAbilityTriggered(Npc npc, ActiveEntity entity, NpcHurtAbility hurtAbility) {
        return new CombatAI(npc, entity, hurtAbility);
    }

    public static CombatAI returnFromEscape(Npc npc, ActiveEntity entity, NpcAbility npcAbility) {
        return new CombatAI(npc, entity, npcAbility);
    }
}

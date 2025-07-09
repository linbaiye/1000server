package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.creatures.npc.event.NpcRemoveEvent;

@Slf4j
public class CombatAI extends AbstractMovableNpcAI {

    private final ActiveEntity enemy;

    private final NpcAttackAbility attackAbility;

    private final HurtAbility enemyHurtAbility;


    private CombatAI(Npc npc, ActiveEntity entity,
                     NpcHurtAbility hurtAbility,
                     NpcAttackAbility attackAbility) {
        super(npc);
        this.enemy = entity;
        changeAbilityOrThrow(NpcHurtAbility.class);
        this.attackAbility = attackAbility;
        hurtAbility.setHurtTrigger(this::onAttacked);
        this.enemyHurtAbility = enemy.findAbility(HurtAbility.class).orElseThrow();
    }

    @Override
    public void update(int delta) {
        attackAbility.cooldown(delta);
        updateAbility(delta);
    }

    private void tryAttack() {
        if (!enemy.canBeSeenAt(npc().coordinate()) || !enemyHurtAbility.canBeAttacked()) {
            log.debug("End combat.");
            npc().changeAI(new WanderingAI(npc(), 10));
            return;
        }
        if (npc().coordinate().directDistance(enemy.coordinate()) >= 2) {
            moveCloser(enemy.coordinate());
            log.debug("Far away, keep moving");
            return;
        }
        Direction direction = npc().coordinate().computeDirection(enemy.coordinate());
        if (direction != npc().direction()) {
            changeAbilityOrThrow(NpcTurnAbility.class)
                    .turn(npc(), direction);
            return;
        }
        if (attackAbility.ableToAttack()) {
            changeAbility(attackAbility);
            attackAbility.apply(npc(), enemy);
        } else {
            stay(attackAbility.cooldown());
        }
    }

    private void stay(int millis) {
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc(), millis);
    }

    private void onMoved(NpcMoveAbility ability) {
        computePrevious();
        if (ability.idleTime() > 0) {
            log.debug("Need to stay idle for {}.", ability.idleTime());
            stay(ability.idleTime());
        } else {
            tryAttack();
        }
    }

    void onAbilityDone(NpcAbility doneAbility) {
        if (doneAbility instanceof NpcMoveAbility moveAbility) {
            onMoved(moveAbility);
        } else if (doneAbility instanceof NpcDieAbility) {
            npc().sendEvent(NpcRemoveEvent.of(npc()));
        } else if (doneAbility instanceof NpcTurnAbility) {
            npc().findAbility(NpcMoveAbility.class).map(NpcMoveAbility::idleTime)
                    .ifPresent(integer -> {
                        if (integer > 0)
                            stay(integer);
                        else
                            tryAttack();
                    });

        } else {
            tryAttack();
        }
    }


    @Override
    void onAfterHurtStart(ActiveEntity attacker, NpcHurtAbility ability) {
        attackAbility.cooldownRecovery();
    }

    @Override
    void onMoveFailed() {
        log.debug("Move failed.");
        npc().findAbility(NpcMoveAbility.class).ifPresentOrElse( moveAbility -> {
            if (moveAbility.idleTime() > 0) {
                changeAbilityOrThrow(NpcIdleAbility.class).apply(npc(), moveAbility.idleTime());
            } else {
                tryAttack();
            }
        }, this::tryAttack);
    }

    public static CombatAI hurtAbilityTriggered(Npc npc, ActiveEntity entity, NpcHurtAbility hurtAbility) {
        var attackAbility = npc.findAbility(NpcAttackAbility.class).orElseThrow();
        attackAbility.cooldownRecovery();
        npc.findAbility(NpcMoveAbility.class).ifPresent(NpcMoveAbility::enableFastMove);
        return new CombatAI(npc, entity, hurtAbility, attackAbility);
    }
}

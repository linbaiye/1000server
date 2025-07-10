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
    private final NpcHurtAbility hurtAbility;
    private final HurtAbility enemyHurtAbility;

    private CombatAI(Npc npc, ActiveEntity entity,
                     NpcHurtAbility hurtAbility) {
        super(npc);
        this.enemy = entity;
        changeAbility(hurtAbility);
        this.hurtAbility = hurtAbility;
        this.attackAbility = npc.findAbility(NpcAttackAbility.class).orElseThrow();
        hurtAbility.setHurtTrigger(this::onAttacked);
        this.enemyHurtAbility = enemy.findAbility(HurtAbility.class).orElseThrow();
    }

    @Override
    public void update(int delta) {
        attackAbility.cooldown(delta);
        hurtAbility.cooldown(delta);
        updateAbility(delta);
    }

    @Override
    public void start() {
        npc().findAbility(NpcMoveAbility.class).ifPresent(NpcMoveAbility::enableFastMove);
    }

    private void tryAttack() {
        if (!enemy.canBeSeenAt(npc().coordinate()) || !enemyHurtAbility.canBeAttacked()) {
            npc().changeAI(new WanderingAI(npc(), 10));
            npc().startAI();
            return;
        }
        if (npc().coordinate().directDistance(enemy.coordinate()) >= 2) {
            moveCloser(enemy.coordinate());
            return;
        }
        Direction direction = npc().coordinate().computeDirection(enemy.coordinate());
        if (direction != npc().direction()) {
            changeAbilityOrThrow(NpcTurnAbility.class)
                    .turn(npc(), direction);
            return;
        }
        if (attackAbility.attackReady() && hurtAbility.cooldownReady()) {
            changeAbility(attackAbility).apply(npc(), enemy);
        } else {
            stay(Math.max(attackAbility.cooldownLeft(), hurtAbility.cooldownLeft()));
        }
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

    void onAbilityDone(NpcAbility doneAbility) {
        if (doneAbility instanceof NpcMoveAbility moveAbility) {
            onMoved(moveAbility);
        } else if (doneAbility instanceof NpcDieAbility) {
            npc().sendEvent(NpcRemoveEvent.of(npc()));
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
}

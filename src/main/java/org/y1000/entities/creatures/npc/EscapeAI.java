package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.util.Coordinate;

@Slf4j
public final class EscapeAI extends AbstractMovableNpcAI {
    private final ActiveEntity enemy;

    private Coordinate destination;

    private final EscapeAbility escapeAbility;

    public EscapeAI(Npc npc, ActiveEntity entity, NpcAbility ability, EscapeAbility escapeAbility) {
        super(npc);
        changeAbility(ability);
        npc.findAbility(NpcHurtAbility.class).ifPresent(hurtAbility -> hurtAbility.setHurtTrigger(this::onAttacked));
        this.enemy = entity;
        this.escapeAbility = escapeAbility;
    }

    private void onAttacked(ActiveEntity entity, NpcHurtAbility hurtAbility) {
        applyHurtAbility(hurtAbility);
    }


    private void computeEscapePoint() {
        destination = escapeAbility.computeSafeSpot(npc(), enemy).orElse(null);
        log.debug("Selected destination {}.", destination);
    }

    @Override
    void onMoveFailed() {
        computeEscapePoint();
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }

    private void returnToWander() {
        if (escapeAbility instanceof LifeLowEscapeAbility)
            npc().startAI(new WaryWanderAI(npc(), currentAbility(), escapeAbility));
        else
            npc().startAI(new WanderingAI(npc()));
    }

    private void returnToWanderOrCombat() {
        if (escapeAbility instanceof LifeLowEscapeAbility)
            npc().startAI(new WaryWanderAI(npc(), currentAbility(), escapeAbility));
        else
            npc().startAI(CombatAI.returnFromEscape(npc(), enemy, currentAbility()));
    }

    @Override
    void onNonDieAbilityDone(NpcAbility ability) {
        if (!enemy.canBeSeenAt(npc().coordinate())) {
            log.debug("No thing to escape, return now.");
            returnToWander();
            return;
        }
        computePrevious();
        if (destination == null) {
            computeEscapePoint();
            log.debug("No point.");
            changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
            return;
        }
        if (npc().coordinate().equals(destination)) {
            returnToWanderOrCombat();
            log.debug("Return now.");
            return;
        }
        if (ability instanceof NpcMoveAbility moveAbility && moveAbility.idleTime() > 0) {
            changeAbilityOrThrow(NpcIdleAbility.class).apply(npc(), moveAbility.idleTime());
        } else {
            log.debug("keep moving at {}.", npc().coordinate());
            moveCloser(destination);
        }
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }

    @Override
    public void start() {
        npc().findAbility(NpcMoveAbility.class).ifPresent(NpcMoveAbility::enableFastMove);
        computeEscapePoint();
    }
}

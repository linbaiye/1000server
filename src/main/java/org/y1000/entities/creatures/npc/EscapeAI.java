package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.util.Coordinate;

@Slf4j
public final class EscapeAI extends AbstractMovableNpcAI {
    private ActiveEntity enemy;

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
        enemy = entity;
        computeEscapePoint();
    }


    private void computeEscapePoint() {
        destination = escapeAbility.computeSafeSpot(npc(), enemy).orElse(null);
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
            npc().startAI(CombatAI.returnFromEscape(npc(), enemy, ));
    }

    @Override
    void onNonDieAbilityDone(NpcAbility ability) {
        if (!enemy.canBeSeenAt(npc().coordinate())) {
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
            log.debug("Wandering again.");
            return;
        }
        if (ability instanceof NpcMoveAbility moveAbility && moveAbility.idleTime() > 0) {
            changeAbilityOrThrow(NpcIdleAbility.class).apply(npc(), moveAbility.idleTime());
        } else {
            moveCloser(destination);
            log.debug("keep moving");
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

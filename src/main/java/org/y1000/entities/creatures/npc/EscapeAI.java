package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.util.Coordinate;

@Slf4j
public final class EscapeAI extends AbstractMovableNpcAI {
    private final ActiveEntity enemy;

    private Coordinate destination;

    private final EscapeAbility escapeAbility;

    public EscapeAI(Npc npc, ActiveEntity entity, NpcUpdatableAbility ability, EscapeAbility escapeAbility) {
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
    }

    @Override
    void onMoveFailed() {
        computeEscapePoint();
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }

    private void returnToWander() {
        npc().startAI(new WaryWanderAI(npc(), currentAbility(), escapeAbility));
    }

    @Override
    void onNonDieAbilityDone(NpcUpdatableAbility ability) {
        if (!enemy.canBeSeenAt(npc().coordinate())) {
            returnToWander();
            return;
        }
        computePrevious();
        if (destination == null) {
            computeEscapePoint();
            changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
            return;
        }
        if (npc().coordinate().equals(destination)) {
            returnToWander();
            return;
        }
        if (ability instanceof NpcMoveAbility moveAbility && moveAbility.idleTime() > 0) {
            changeAbilityOrThrow(NpcIdleAbility.class).apply(npc(), moveAbility.idleTime());
        } else {
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

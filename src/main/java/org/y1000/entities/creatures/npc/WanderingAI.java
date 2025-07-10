package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.util.Coordinate;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class WanderingAI extends AbstractMovableNpcAI {

    private Coordinate destination;

    private final int wanderRange;

    public WanderingAI(Npc npc,
                       int wanderRange) {
        super(npc);
        this.wanderRange = wanderRange;
        npc.findAbility(NpcHurtAbility.class).ifPresent(h -> h.setHurtTrigger(this::onAttacked));
    }

    private Coordinate chooseTarget(Coordinate origin) {
        int minX = Math.max(0, origin.x() - wanderRange);
        int maxX = origin.x() + wanderRange;
        var x = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
        int minY = Math.max(0, origin.y() - wanderRange);
        int maxY = origin.y() + wanderRange;
        var y = ThreadLocalRandom.current().nextInt(minY, maxY + 1);
        return new Coordinate(x, y);
    }

    private void initialize() {
        computePrevious();
        this.destination = chooseTarget(npc().getSpawnCoordinate());
        changeAbilityOrThrow(NpcIdleAbility.class)
                .apply(npc());
    }


    private void onMoveDone() {
        computePrevious();
        if (npc().coordinate().equals(destination)) {
            log.debug("Arrived, set target to {}.", destination);
            destination = chooseTarget(npc().getSpawnCoordinate());
        }
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }

    private void onTurnDone() {
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }


    private void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        applyHurtAbility(ability);
        if (attacker.findAbility(HurtAbility.class).map(HurtAbility::canBeAttacked).orElse(false) &&
                npc().findAbility(NpcAttackAbility.class).isPresent()) {
            npc().changeAI(CombatAI.hurtAbilityTriggered(npc(), attacker, ability));
            npc().startAI();
        }
    }

    void onAbilityDone(NpcAbility doneAbility) {
        if (doneAbility instanceof NpcMoveAbility) {
            onMoveDone();
        } else if (doneAbility instanceof NpcIdleAbility) {
            onIdleDone();
        } else if (doneAbility instanceof NpcTurnAbility) {
            onTurnDone();
        }
    }


    private void onIdleDone() {
        moveCloser(destination);
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }

    @Override
    public void start() {
        npc().findAbility(NpcMoveAbility.class).ifPresent(NpcMoveAbility::disableFastMove);
        initialize();
    }

    @Override
    void onMoveFailed() {
        initialize();
    }
}

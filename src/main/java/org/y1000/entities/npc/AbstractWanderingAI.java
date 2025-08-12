package org.y1000.entities.npc;

import org.y1000.util.Coordinate;
import org.y1000.util.Counter;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWanderingAI extends AbstractMovableNpcAI {

    private Coordinate destination;

    private final Counter counter;

    public AbstractWanderingAI(Npc npc) {
        super(npc);
        counter = Counter.of(10);
    }


    abstract Coordinate getWanderOrigin();

    private Coordinate chooseTarget(Coordinate origin) {
        var wanderRange = npc().getWanderRage();
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
        this.destination = chooseTarget(getWanderOrigin());
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }

    void onMoveDone() {
        computePrevious();
        if (counter.count(1)) {
            counter.reset();
            npc().findAbility(NpcSoundAbility.class).ifPresent(a -> a.trySound(npc()));
        }
        if (npc().coordinate().equals(destination)) {
            destination = chooseTarget(getWanderOrigin());
        }
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }

    void onTurnDone() {
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }

    void onIdleDone() {
        moveCloser(destination);
    }

    void continueWander(NpcAnimatedAbility doneAbility) {
        if (doneAbility instanceof NpcMoveAbility) {
            onMoveDone();
        } else if (doneAbility instanceof NpcTurnAbility) {
            onTurnDone();
        } else {
            onIdleDone();
        }
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

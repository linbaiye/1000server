package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
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
        initialize();
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

    public void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        if (currentAbility() instanceof NpcMoveAbility moveAbility) {
            moveAbility.interrupt(npc());
        }
        ability.apply(npc(), currentAbility());
        npc().findAbility(NpcAttackAbility.class).ifPresentOrElse(a -> npc().changeAI(new CombatAI(npc(), attacker, ability)),
                () -> changeAbilityOrThrow(NpcHurtAbility.class));
    }

    private void onAbilityDone(NpcAbility doneAbility) {
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
        /*var dir = AiPathUtil.computeNextMoveDirection(npc(), destination, previous);
        if (dir == null) {
            log.debug("No direction, set next target to {}.", destination);
            initialize();
            return;
        }
        if (dir == npc().direction()) {
            if (!changeAbilityOrThrow(NpcMoveAbility.class)
                    .tryNormalMove(npc(), dir)) {
                initialize();
            }
        } else {
            changeAbilityOrThrow(NpcTurnAbility.class).turn(npc(), dir);
        }*/
    }


    @Override
    public void update(int delta) {
        if (!currentAbility().update(delta)) {
            return;
        }
        if (currentAbility() instanceof NpcHurtAbility hurtAbility) {
            onAbilityDone(hurtAbility.getInterruptedAbility());
        } else {
            onAbilityDone(currentAbility());
        }
    }

    @Override
    void noDirection() {
        initialize();
    }

    @Override
    void directionNotMovable() {
        initialize();
    }
}

package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.AI.AiPathUtil;
import org.y1000.message.NpcSnapshot;
import org.y1000.util.Coordinate;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class WanderingAI implements NpcAI {

    private final Npc npc;

    private Coordinate target;

    private final int wanderRange;

    private Coordinate previous;

    private NpcAbility currentAbility;

    public WanderingAI(Npc npc,
                       int wanderRange) {
        this.npc = npc;
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
        this.target = chooseTarget(npc.getSpawnCoordinate());
        this.previous = npc.coordinate().moveBy(npc.direction().opposite());
        changeAbilityOrThrow(NpcIdleAbility.class)
                .apply(npc);
    }


    private <T extends NpcAbility> T changeAbilityOrThrow(Class<T> type) {
        var a = npc.findAbility(type).orElseThrow();
        currentAbility = a;
        return a;
    }


    private void onMoveDone() {
        if (npc.coordinate().equals(target)) {
            log.debug("Arrived, set target to {}.", target);
            target = chooseTarget(npc.getSpawnCoordinate());
        }
        previous = npc.coordinate().moveBy(npc.direction().opposite());
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc);
    }

    private void onTurnDone() {
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc);
    }

    public void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        if (currentAbility instanceof NpcMoveAbility moveAbility) {
            moveAbility.interrupt(npc);
        }
//        if (ability.getCurrentLife() == 0) {
//            npc.findAction(DieAction.class).ifPresent(dieAbility -> dieAbility.die(npc));
//            return;
//        }
        ability.apply(npc, currentAbility);
        currentAbility = ability;
//        npc.findAbility(NpcAttackAbility.class)
//                .ifPresentOrElse(a -> npc.changeAI(new CombatAI(npc, currentAction, attacker)), this::swallowAttacked);
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
        var dir = AiPathUtil.computeNextMoveDirection(npc, target, previous);
        if (dir == null) {
            log.debug("No direction, set next target to {}.", target);
            initialize();
            return;
        }
        if (dir == npc.direction()) {
            if (!changeAbilityOrThrow(NpcMoveAbility.class)
                    .tryNormalMove(npc, dir)) {
                initialize();
            }
        } else {
            changeAbilityOrThrow(NpcTurnAbility.class).turn(npc, dir);
        }
    }

    /*
        private void onActionDone(NpcActionEnum actionEnum) {
        switch (actionEnum) {
            case Idle -> nextMove();
            case Move -> onMoveDone();
            case Turn -> onTurnDone();
        }
     */

    @Override
    public void update(int delta) {
        if (!currentAbility.update(delta)) {
            return;
        }
        if (currentAbility instanceof NpcHurtAbility hurtAbility) {
            onAbilityDone(hurtAbility.getInterruptedAbility());
        } else {
            onAbilityDone(currentAbility);
        }
    }

    @Override
    public NpcSnapshot captureSnapshot() {
        return currentAbility.captureSnapshot(npc);
    }
}

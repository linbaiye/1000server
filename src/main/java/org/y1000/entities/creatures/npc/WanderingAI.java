package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.NpcMoveAbility;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.AI.AiPathUtil;
import org.y1000.entities.players.MoveAction;
import org.y1000.util.Coordinate;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class WanderingAI implements NpcAI {
    private final Npc npc;

    private NpcAction currentAction;

    private Coordinate target;

    private final int wanderRange;

    private Coordinate previous;

    public WanderingAI(Npc npc, int wanderRange) {
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
        npc.findAction(IdleAction.class).ifPresentOrElse(idleAction -> {
            currentAction = idleAction;
            idleAction.stayLoopAnimationMillis(npc);
        }, () -> new RuntimeException("No idle state."));
        this.target = chooseTarget(npc.getSpawnCoordinate());
        this.previous = npc.coordinate().moveBy(npc.direction().opposite());
    }


    private void nextMove() {
        var dir = AiPathUtil.computeNextMoveDirection(npc, target, previous);
        if (dir == null) {
            log.debug("No direction, set next target to {}.", target);
            initialize();
            return;
        }
        if (dir == npc.direction()) {
            npc.findAction(NpcMoveAbility.class).ifPresent(moveAction -> {
                currentAction = moveAction;
                if (!moveAction.tryNormalMove(npc, dir)) {
                    log.debug("{} no movable, reset destination .", target);
                    initialize();
                }
            });
        } else {
            npc.findAction(TurnAction.class).ifPresent(turnAction -> {
                currentAction = turnAction;
                turnAction.turn(npc, dir);
            });
        }
    }

    private void onMoveDone() {
        if (npc.coordinate().equals(target)) {
            log.debug("Arrived, set target to {}.", target);
            target = chooseTarget(npc.getSpawnCoordinate());
        }
        previous = npc.coordinate().moveBy(npc.direction().opposite());
        npc.findAction(IdleAction.class).ifPresent(idleAction -> {
            currentAction = idleAction;
            idleAction.stayLoopAnimationMillis(npc);
        });
    }

    private void onTurnDone() {
        npc.findAction(IdleAction.class).ifPresent(idleAction -> {
            currentAction = idleAction;
            idleAction.stayLoopAnimationMillis(npc);
        });
    }


    public void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        if (currentAction instanceof NpcMoveAbility moveAction) {
            moveAction.interrupt(npc);
        }
        if (ability.getCurrentLife() == 0) {
            npc.findAction(DieAction.class).ifPresent(dieAbility -> dieAbility.die(npc));
            return;
        }
        npc.findAbility(NpcAttackAbility.class)
                .ifPresentOrElse(a -> npc.changeAI(new CombatAI(npc, currentAction, attacker)), this::swallowAttacked);
    }


    private void swallowAttacked()  {
    }


    private void onActionDone(NpcActionEnum actionEnum) {

    }


    @Override
    public void update(int delta) {
        if (!currentAction.update(delta)) {
            return;
        }
        if (currentAction instanceof HurtAction hurtAbility) {
            onActionDone(hurtAbility.getPreviousAction());
        } else {
            onActionDone(currentAction.actionEnum());
        }
    }

    @Override
    public NpcAction currentAction() {
        return currentAction;
    }
}

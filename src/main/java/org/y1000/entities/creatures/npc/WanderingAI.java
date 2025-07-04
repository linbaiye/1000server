package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.MoveAction;
import org.y1000.entities.creatures.npc.AI.AiPathUtil;
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
            initialize();
            return;
        }
        if (dir == npc.direction()) {
            npc.findAction(MoveAction.class).ifPresent(moveAction -> {
                currentAction = moveAction;
                if (!moveAction.tryNormalMove(npc, dir)) {
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


    public void onAttacked(HurtAbility action) {
        if (currentAction instanceof MoveAction moveAction) {
            moveAction.interrupt(npc);
        }
        if (action.getCurrentLife() == 0) {
            npc.findAction(DieAbility.class).ifPresent(dieAbility -> dieAbility.die(npc));
        } else {
            npc.findAction(AttackAction.class).ifPresentOrElse(a -> npc.changeAI(new FightAI(npc)), () -> {
                currentAction = action;
                action.hurt(npc);
            });
        }
    }

    @Override
    public void update(int delta) {
        if (!currentAction.update(delta)) {
            return;
        }
        switch (currentAction.actionEnum()) {
            case Idle, Hurt -> nextMove();
            case Move -> onMoveDone();
            case Turn -> onTurnDone();
        }
    }

    @Override
    public NpcAction currentAction() {
        return currentAction;
    }
}

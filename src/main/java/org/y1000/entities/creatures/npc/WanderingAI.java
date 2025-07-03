package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.MoveAction;
import org.y1000.entities.creatures.npc.AI.AiPathUtil;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

public class WanderingAI implements NpcAI {
    private final Npc npc;

    private NpcAction currentAction;

    private Coordinate target;

    private final Rectangle wanderArea;

    private Coordinate previous;

    public WanderingAI(Npc npc, Rectangle wanderArea) {
        this.npc = npc;
        this.wanderArea = wanderArea;
    }


    private void setup() {
        npc.findAction(IdleAction.class).ifPresentOrElse(idleAction -> {
            currentAction = idleAction;
            idleAction.stay(npc.direction());
        }, () -> new RuntimeException("No idle state."));
        this.target = wanderArea.random(npc.getSpawnCoordinate());
        this.previous = npc.coordinate().moveBy(npc.direction().opposite());
    }


    private void nextMove() {
        var dir = AiPathUtil.computeNextMoveDirection(npc, target, previous);
        if (dir == null) {
            setup();
            return;
        }
        if (dir == npc.direction()) {
            npc.findAction(MoveAction.class).ifPresent(moveAction -> {
                if (moveAction.tryNormalMove(npc, dir)) {
                    currentAction = moveAction;
                } else {
                    setup();
                }
            });
        } else {
            npc.findAction(TurnAction.class).ifPresent(turnAction -> {
                currentAction = turnAction;
                turnAction.turn(npc);
            });
        }
    }

    private void onMoveDone() {
        npc.findAction(IdleAction.class).ifPresent(idleAction -> {
            currentAction = idleAction;
            idleAction.stay(npc.direction());
        });
    }


    public void onAttacked(HurtAbility action) {
        if (currentAction instanceof MoveAction moveAction) {
            moveAction.hurt(npc);
        }
        if (action.getCurrentLife() == 0) {
            npc.findAction(DieAbility.class).ifPresent(dieAbility -> dieAbility.die(npc));
        } else {
            npc.findAction(AttackAction.class).ifPresent(a -> npc.changeAI(new FightAI(npc)));
        }
    }

    @Override
    public void update(int delta) {
        if (!currentAction.update(delta)) {
            return;
        }
        switch (currentAction.actionEnum()) {
            case Idle, Turn -> nextMove();
            case Move -> onMoveDone();
        }
    }

    @Override
    public NpcAction currentAction() {
        return currentAction;
    }
}

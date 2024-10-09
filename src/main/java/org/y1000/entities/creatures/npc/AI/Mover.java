package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

import java.util.function.Consumer;

@Slf4j
class Mover<N extends Npc> {
    private Coordinate destination;

    private Coordinate previous;

    private int moveMillis;

    private int idleMillis;

    private final N npc;

    private int totalMillis;

    private boolean walk;

    private Mover(N npc,
                  Coordinate destination,
                  boolean walk) {
        Validate.notNull(npc);
        Validate.notNull(destination);
        this.npc = npc;
        this.destination = destination;
        this.previous = Coordinate.Empty;
        this.totalMillis = 0;
        this.walk = walk;
        if (walk)
            computeWalkMillis();
        else
            computeRunMillis();
    }

    private void doMove(Consumer<? super N> noPathAction) {
        var direction = AiPathUtil.computeNextMoveDirection(npc, destination, previous);
        if (direction == null) {
            noPathAction.accept(npc);
            return;
        }
        if (direction != npc.direction()) {
            npc.changeDirection(direction);
            npc.emitEvent(SetPositionEvent.of(npc));
            if (idleMillis > 0) {
                log.debug("Stay");
                npc.stay(idleMillis);
                totalMillis += idleMillis;
                return;
            }
        }
        if (npc.realmMap().movable(npc.coordinate().moveBy(direction))) {
            log.debug("Move");
            npc.move(moveMillis);
            totalMillis += moveMillis;
        } else {
            noPathAction.accept(npc);
        }
    }


    public int usedMillis() {
        return totalMillis;
    }


    public void nextMove(Consumer<? super N> noPathAction) {
        if (isArrived())
            return;
        if (npc.stateEnum() == State.WALK) {
            log.debug("Walk done.");
            previous = npc.coordinate().moveBy(npc.direction().opposite());
        }
        if (npc.stateEnum() == State.IDLE) {
            log.debug("Idle done.");
            doMove(noPathAction);
            return;
        }
        if (idleMillis > 0) {
            log.debug("Stay idle.");
            npc.stay(idleMillis);
            totalMillis += idleMillis;
        } else {
            doMove(noPathAction);
        }
    }

    public void walk(Consumer<? super N> noPathAction) {
        if (!walk) {
            walk = true;
            computeWalkMillis();
        }
        nextMove(noPathAction);
    }

    public void run(Consumer<? super N> noPathAction) {
        if (walk) {
            walk = false;
            computeRunMillis();
        }
        nextMove(noPathAction);
    }

    public boolean isArrived() {
        return npc.coordinate().equals(destination);
    }

    public void changeDestination(Coordinate coordinate) {
        Validate.notNull(coordinate);
        destination = coordinate;
        previous = Coordinate.Empty;
        totalMillis = 0;
    }

    private void computeWalkMillis() {
        int walkSpeed = npc.walkSpeed();
        var stateMillis = npc.getStateMillis(State.WALK);
        int walkMillis = Math.min(stateMillis, walkSpeed);
        moveMillis = Math.max(walkMillis, 200);
        idleMillis = Math.max(walkSpeed - walkMillis, npc.getStateMillis(State.IDLE));
    }

    private void computeRunMillis() {
        int walkSpeed = npc.walkSpeed() / 2;
        var stateMillis = npc.getStateMillis(State.WALK);
        int walkMillis = Math.min(stateMillis, walkSpeed);
        moveMillis = Math.max(walkMillis, 200);
        idleMillis = walkSpeed - walkMillis;
    }

    public static <N extends Npc> Mover<N> walk(N npc, Coordinate destination) {
        return new Mover<>(npc, destination, true);
    }

    public static <N extends Npc> Mover<N> run(N npc, Coordinate destination) {
        return new Mover<>(npc, destination, false);
    }
}

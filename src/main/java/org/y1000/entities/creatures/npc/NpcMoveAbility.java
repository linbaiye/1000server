package org.y1000.entities.creatures.npc;


import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.event.NpcMoveEvent;
import org.y1000.entities.creatures.npc.event.NpcMovedEvent;
import org.y1000.util.Coordinate;


public class NpcMoveAbility extends AbstractNpcAbility {

    private final int walkSpeedMillis;

    private Coordinate start;

    private Direction direction;

    private Npc npc;

    private boolean fastMove;

    /*
     * If negative, the npc will finish moving even before the animation is done.
     */
    private final int diffMillis;

    public NpcMoveAbility(int walkSpeedMillis, NpcAnimation timer) {
        super(timer);
        this.walkSpeedMillis = walkSpeedMillis;
        diffMillis = walkSpeedMillis - timer.getActualMillis();
    }

    public void interrupt(Npc npc) {
        var end = start.moveBy(direction);
        npc.changeCoordinate(end);
        npc.sendEvent(NpcMovedEvent.of(npc));
    }

    @Override
    public boolean update(int delta) {
        if (!updateAnimation(delta))
            return false;
        if (!npc.getRealmMap().movable(start.moveBy(direction))) {
            npc.changeCoordinate(start);
        } else {
            npc.changeCoordinate(start.moveBy(direction));
        }
        npc.sendEvent(NpcMovedEvent.of(npc));
        return true;
    }

    public int idleTime() {
        return diffMillis;
    }

    public void disableFastMove() {
        fastMove = false;
    }

    /**
     * The time to move one unit could be shorter than animation time.
     */
    public void enableFastMove() {
        this.fastMove = true;
    }


    public boolean tryMove(Npc npc, Direction direction) {
        if (!npc.getRealmMap().movable(npc.coordinate().moveBy(direction))) {
            return false;
        }
        this.direction = direction;
        this.start = npc.coordinate();
        this.npc = npc;
        if (fastMove && diffMillis < 0)
            startAnimation(walkSpeedMillis);
        else
            startAnimation();
        npc.changeDirection(direction);
        npc.sendEvent(NpcMoveEvent.of(npc));
        return true;
    }
}

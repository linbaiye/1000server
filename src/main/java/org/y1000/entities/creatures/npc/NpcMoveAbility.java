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

    public NpcMoveAbility(int walkSpeedMillis, NpcAnimation timer) {
        super(timer);
        this.walkSpeedMillis = walkSpeedMillis;
    }

    public void interrupt(Npc npc) {
        var end = start.moveBy(direction);
        npc.setCoordinate(end);
        npc.sendEvent(NpcMovedEvent.of(npc));
    }

    @Override
    public boolean update(int delta) {
        if (!updateAnimation(delta))
            return false;
        if (!npc.getRealmMap().movable(start.moveBy(direction))) {
            npc.setCoordinate(start);
        } else {
            npc.setCoordinate(start.moveBy(direction));
        }
        npc.sendEvent(NpcMovedEvent.of(npc));
        return true;
    }

    public boolean tryNormalMove(Npc npc, Direction direction) {
        var ret = tryMove(npc, direction);
        startAnimation();
        return ret;
    }

    public boolean tryFastMove(Npc npc, Direction direction) {
        var ret = tryMove(npc, direction);
        startAnimation(walkSpeedMillis);
        return ret;
    }


    private boolean tryMove(Npc npc, Direction direction) {
        if (!npc.getRealmMap().movable(npc.coordinate().moveBy(direction))) {
            return false;
        }
        this.direction = direction;
        this.start = npc.coordinate();
        this.npc = npc;
        npc.changeDirection(direction);
        npc.sendEvent(NpcMoveEvent.of(npc));
        return true;
    }
}

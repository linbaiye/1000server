package org.y1000.entities.npc;


import org.y1000.entities.Direction;
import org.y1000.entities.npc.event.NpcMoveEvent;
import org.y1000.entities.npc.event.NpcMovedEvent;
import org.y1000.entities.npc.event.NpcSnapshot;
import org.y1000.util.Coordinate;


public class NpcMoveAbility extends AbstractNpcAbility {


    private Coordinate start;

    private Direction direction;

    private Npc npc;

    /*
     * For some NPC, its walkSpeedMillis is longer than the actual walk animation length,
     * hence some idle animation interpolated.
     */
    private int interpolateIdleMillis;

    private final int walkSpeedMillis;

    private int moveMillis;

    public NpcMoveAbility(int walkSpeedMillis, NpcAnimation animation) {
        super(animation);
        this.walkSpeedMillis = walkSpeedMillis;
        disableFastMove();
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
        if (!npc.realmMap().movable(start.moveBy(direction))) {
            npc.changeCoordinate(start);
        } else {
            npc.changeCoordinate(start.moveBy(direction));
        }
        npc.sendEvent(NpcMovedEvent.of(npc));
        return true;
    }

    @Override
    public NpcSnapshot captureSnapshot(Npc npc) {
        return NpcSnapshot.of(npc, getAnimation().elapsedMillis(), getAnimation().type(), moveMillis);
    }

    public int idleTime() {
        return interpolateIdleMillis;
    }

    public void disableFastMove() {
        modifyMoveSpeed(2);
        if (moveMillis < getAnimation().getActualMillis())
            moveMillis = getAnimation().getActualMillis();
    }

    private void modifyMoveSpeed(int multiplier) {
        moveMillis = this.walkSpeedMillis * multiplier;
        interpolateIdleMillis = moveMillis - getAnimation().getActualMillis();
        if (moveMillis > getAnimation().getActualMillis())
            moveMillis = getAnimation().getActualMillis();
    }


    public void enableFastMove() {
        modifyMoveSpeed(1);
    }


    public boolean tryMove(Npc npc, Direction direction) {
        if (!npc.realmMap().softOccupy(npc, npc.coordinate().moveBy(direction))) {
            return false;
        }
        this.direction = direction;
        this.start = npc.coordinate();
        this.npc = npc;
        startAnimation(moveMillis);
        npc.changeDirection(direction);
        npc.sendEvent(NpcMoveEvent.of(npc, moveMillis));
        return true;
    }
}

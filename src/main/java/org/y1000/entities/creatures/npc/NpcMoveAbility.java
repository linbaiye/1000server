package org.y1000.entities.creatures.npc;


import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.event.NpcMoveEvent;
import org.y1000.entities.creatures.npc.event.NpcMovedEvent;
import org.y1000.util.Coordinate;


public class NpcMoveAbility extends AbstractNpcAbility {

    private int fastMoveMillis;

    private Coordinate start;

    private Direction direction;

    private Npc npc;

    private boolean fastMove;

    /*
     * For some NPC, its walkSpeedMillis is longer than the actual walk animation length,
     * hence some idle animation interpolated.
     */
    private int interpolateIdleMillis;

    private final int walkMillis;


    public NpcMoveAbility(int walkSpeedMillis, NpcAnimation animation) {
        super(animation);
        this.walkMillis = walkSpeedMillis;
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
        return interpolateIdleMillis;
    }

    public void disableFastMove() {
        fastMove = false;
    }

    private void enableFastMove(float modifier) {
        this.fastMove = true;
        this.fastMoveMillis = (int)((float)walkMillis / modifier);
        interpolateIdleMillis = fastMoveMillis - getAnimation().getActualMillis();
    }

    /**
     * The time to move one unit could be shorter than animation time.
     */
    public void enableCombatMove() {
        enableFastMove(3);
    }

    public void enableEscapeMove() {
        enableFastMove(1.5f);
    }

    public boolean tryMove(Npc npc, Direction direction) {
        if (!npc.getRealmMap().movable(npc.coordinate().moveBy(direction))) {
            return false;
        }
        this.direction = direction;
        this.start = npc.coordinate();
        this.npc = npc;
        if (fastMove)
            startAnimation(fastMoveMillis);
        else
            startAnimation();
        npc.changeDirection(direction);
        npc.sendEvent(NpcMoveEvent.of(npc));
        return true;
    }
}

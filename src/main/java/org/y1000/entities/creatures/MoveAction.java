package org.y1000.entities.creatures;


import lombok.Getter;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcAction;
import org.y1000.util.Coordinate;


public class MoveAction implements NpcAction {

    private final int animationMillis;
    private final int walkSpeed;

    private int thresholdMillis;

    private int elapsedMillis;

    private Coordinate start;

    private Direction direction;

    @Getter
    private boolean moved;

    private Npc npc;

    public MoveAction(int animationMillis, int walkSpeed) {
        this.animationMillis = animationMillis;
        this.walkSpeed = walkSpeed;
    }

    public void hurt(Npc npc) {
        var end = start.moveBy(direction);
        if (elapsedMillis >= animationMillis / 2 && npc.getRealmMap().movable(end))
            npc.setCoordinate(end);
        else
            npc.setCoordinate(start);
    }

    @Override
    public boolean update(int delta) {
        elapsedMillis += delta;
        if (elapsedMillis < thresholdMillis) {
            return false;
        }
        if (elapsedMillis > thresholdMillis) {
            return true;
        }
        if (!npc.getRealmMap().movable(start.moveBy(direction))) {
            npc.setCoordinate(start);
            moved = false;
        } else {
            npc.setCoordinate(start.moveBy(direction));
            moved = true;
        }
        return true;
    }

    @Override
    public int elapsedMillis() {
        return elapsedMillis;
    }

    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Move;
    }

    public boolean tryNormalMove(Npc npc, Direction direction) {
        return tryMove(npc, direction, animationMillis);
    }

    public boolean tryFastMove(Npc npc, Direction direction) {
        return tryMove(npc, direction, Math.min(animationMillis, walkSpeed));
    }

    private boolean tryMove(Npc npc, Direction direction, int millis) {
        if (!npc.getRealmMap().movable(npc.coordinate().moveBy(direction))) {
            return false;
        }
        this.direction = direction;
        this.start = npc.coordinate();
        this.elapsedMillis = 0;
        this.thresholdMillis = millis;
        this.npc = npc;
        return true;
    }
}

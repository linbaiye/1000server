package org.y1000.message;

import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.network.gen.InterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

public abstract class AbstractCreatureSnapshot extends AbstractEntitySnapshot {

    private final int stateValue;
    private final Direction direction;
    private final int elapsedMillis;
    private InterpolationPacket interpolationPacket;

    private final int moveAction;

    public AbstractCreatureSnapshot(long id, Coordinate coordinate, PlayerStateEnum playerStateEnum, Direction direction, int elapsedMillis) {
        super(id, coordinate);
        this.stateValue = playerStateEnum.value();
        this.direction = direction;
        this.elapsedMillis = elapsedMillis;
        moveAction = 0;
    }

    public AbstractCreatureSnapshot(long id, Coordinate coordinate, int stateValue, Direction direction, int elapsedMillis, int moveAction) {
        super(id, coordinate);
        this.stateValue = stateValue;
        this.direction = direction;
        this.elapsedMillis = elapsedMillis;
        this.moveAction = moveAction;
    }

    InterpolationPacket interpolationPacket() {
        if (interpolationPacket == null) {
            interpolationPacket = InterpolationPacket.newBuilder()
                    .setY(coordinate().y())
                    .setX(coordinate().x())
                    .setState(stateValue)
                    .setElapsedMillis(elapsedMillis)
                    .setDirection(direction.value())
                    .setMoveAction(moveAction)
                    .build();
        }
        return interpolationPacket;
    }
}

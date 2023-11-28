package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

public abstract class AbstractMovementMessage implements Message {
    private final Direction direction;
    private final Coordinate coordinate;
    private final long sourceId;
    private final long timestamp;

    public AbstractMovementMessage(Direction direction, Coordinate coordinate, long sourceId) {
        this(direction, coordinate, sourceId, System.currentTimeMillis());
    }

    public AbstractMovementMessage(Direction direction, Coordinate coordinate, long sourceId, long timestamp) {
        this.direction = direction;
        this.coordinate = coordinate;
        this.sourceId = sourceId;
        this.timestamp = timestamp;
    }

}

package org.y1000.message;

import org.y1000.util.Coordinate;

public record SetCoordinateMessage(long messageId, Coordinate coordinate) implements Message {

    @Override
    public MessageType type() {
        return MessageType.MOVE;
    }

}

package org.y1000.message;

import org.y1000.entities.Direction;

public record MoveMessage(Direction direction, long timestamp) implements Message {
    @Override
    public long messageId() {
        return 0;
    }

    @Override
    public MessageType type() {
        return null;
    }
}

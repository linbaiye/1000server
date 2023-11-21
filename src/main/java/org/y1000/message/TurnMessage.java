package org.y1000.message;

public record TurnMessage(long messageId) implements Message {

    @Override
    public MessageType type() {
        return MessageType.TURN;
    }
}

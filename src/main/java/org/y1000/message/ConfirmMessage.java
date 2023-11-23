package org.y1000.message;

public record ConfirmMessage(long messageId, long timestamp, long sourceId) implements Message {
    @Override
    public MessageType type() {
        return MessageType.CONFIRM;
    }
}

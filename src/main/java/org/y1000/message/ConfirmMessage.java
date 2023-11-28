package org.y1000.message;

import org.y1000.connection.gen.Packet;

public record ConfirmMessage(long messageId, long timestamp, long sourceId) implements Message {
    @Override
    public MessageType type() {
        return MessageType.CONFIRM;
    }

    @Override
    public Packet toPacket() {
        return null;
    }
}

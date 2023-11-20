package org.y1000.message;

public record ConfirmMessage(long messageId) implements Message {
    @Override
    public MessageType type() {
        return MessageType.CONFIRM;
    }

    @Override
    public void accept(MessageHandler visitor) {

    }
}

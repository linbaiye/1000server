package org.y1000.message;

import java.util.Optional;

public interface Message {
    long sourceId();

    MessageType type();

    long timestamp();

    default Optional<Message> dispatch(MessageHandler handler) {
        return handler.handle(this);
    }
}

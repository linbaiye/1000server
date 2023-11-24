package org.y1000.message;

public interface Message {
    long sourceId();

    MessageType type();

    long timestamp();
}

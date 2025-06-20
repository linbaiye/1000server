package org.y1000.message.input;

public record ClientDragPlayerEvent(long target, int ropeSlot) implements ClientEvent {
}

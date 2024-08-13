package org.y1000.message.clientevent;

public record ClientDragPlayerEvent(long target, int ropeSlot) implements ClientEvent {
}

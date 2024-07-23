package org.y1000.message.clientevent;

public record ClientTriggerDynamicObjectEvent(long id, int useSlot) implements ClientEvent {
}

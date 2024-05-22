package org.y1000.message.clientevent;

public record ClientClickedInventorySlotEvent(int sourceSlot) implements ClientInventoryEvent{
}

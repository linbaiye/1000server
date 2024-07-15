package org.y1000.message.clientevent;

public record ClientAddTradeItemEvent(int slot, long number) implements ClientEvent {
}

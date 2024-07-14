package org.y1000.message.clientevent;

public record ClientTradePlayerEvent(long targetId, int slot) implements ClientEvent {
}

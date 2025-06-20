package org.y1000.message.input;

public record ClientTradePlayerEvent(long targetId, int slot) implements ClientEvent {
}

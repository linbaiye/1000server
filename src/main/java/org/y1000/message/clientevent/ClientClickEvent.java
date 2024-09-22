package org.y1000.message.clientevent;

public record ClientClickEvent(long clickedId) implements ClientEvent {
}

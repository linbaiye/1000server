package org.y1000.message.clientevent;

import org.y1000.util.Coordinate;

public record ClientFoundGuildEvent(String name, Coordinate coordinate, int inventorySlot) implements ClientEvent {

}

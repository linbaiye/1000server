package org.y1000.message.clientevent;


import org.y1000.util.Coordinate;

public record ClientDropItemEvent(int number, int sourceSlot, int x, int y, Coordinate coordinate) implements ClientInventoryEvent{

}

package org.y1000.message.clientevent;

import org.y1000.util.Coordinate;

public record ClientSitDownEvent(Coordinate coordinate) implements ClientEvent {

}

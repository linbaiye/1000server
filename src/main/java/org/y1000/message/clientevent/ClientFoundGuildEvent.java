package org.y1000.message.clientevent;

import org.y1000.network.gen.ClientFoundGuildPacket;
import org.y1000.util.Coordinate;

public record ClientFoundGuildEvent(String name, Coordinate coordinate, int inventorySlot) implements ClientEvent {

    public static ClientFoundGuildEvent parse(ClientFoundGuildPacket packet) {
        return new ClientFoundGuildEvent(packet.getName(), Coordinate.xy(packet.getX(), packet.getY()), packet.getInventorySlot());
    }

}

package org.y1000.message.clientevent;

import org.y1000.message.RightClickType;
import org.y1000.network.gen.RightClickPacket;

public record ClientRightClickEvent(RightClickType type, int slotId, int page) implements ClientEvent {

    public static ClientRightClickEvent fromPacket(RightClickPacket packet) {
        RightClickType type = RightClickType.fromValue(packet.getType());
        return new ClientRightClickEvent(type, packet.getSlotId(), packet.getPage());
    }
}

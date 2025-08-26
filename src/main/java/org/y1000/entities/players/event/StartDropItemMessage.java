package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.StartDopItemPacket;
import org.y1000.util.Coordinate;

public class StartDropItemMessage extends Abstract2PlayerMessageEvent {
    public StartDropItemMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static StartDropItemMessage of(Player player, int slot, Item item, Coordinate at) {
        StartDopItemPacket packet = StartDopItemPacket.newBuilder()
                .setMaxNumber((int)(item instanceof StackItem stackItem ? stackItem.number() : 1))
                .setName(item.name())
                .setX(at.x())
                .setY(at.y())
                .setSlot(slot)
                .build();
        return new StartDropItemMessage(player, Packet.newBuilder().setDropItemPacket(packet).build());
    }
}

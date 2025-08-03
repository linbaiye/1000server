package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.item.Pill;
import org.y1000.item.StackItem;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PillsPacket;

public class PillsMessage extends Abstract2PlayerMessageEvent {
    public PillsMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static PillsMessage of(Player player) {
        var builder = PillsPacket.newBuilder();
        player.inventory().foreach((s, i) -> {
            if (i instanceof StackItem stackItem && stackItem.item() instanceof Pill) {
                builder.addPills(i.name());
            }
        });
        return new PillsMessage(player, Packet.newBuilder().setPills(builder.build()).build());
    }
}

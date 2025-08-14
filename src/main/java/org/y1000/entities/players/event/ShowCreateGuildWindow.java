package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;

public class ShowCreateGuildWindow extends Abstract2PlayerMessageEvent {
    public ShowCreateGuildWindow(Player player, Packet packet) {
        super(player, packet);
    }
}

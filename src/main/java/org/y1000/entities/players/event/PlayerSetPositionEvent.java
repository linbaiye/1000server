package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;

public class PlayerSetPositionEvent extends Abstract2VisiblePlayersMessageEvent {
    public PlayerSetPositionEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerSetPositionEvent of(Player player) {
        return new PlayerSetPositionEvent();
    }
}

package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.CloseTradeWindowPacket;
import org.y1000.network.gen.Packet;

public class CloseTradeWindowMessage extends Abstract2PlayerMessageEvent {

    public CloseTradeWindowMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static CloseTradeWindowMessage playerTrade(Player player) {
        return new CloseTradeWindowMessage(player, Packet.newBuilder().setCloseTradePacket(CloseTradeWindowPacket.newBuilder()
                .setWhich(1)
                .build()).build());
    }

    public static CloseTradeWindowMessage bank(Player player) {
        return new CloseTradeWindowMessage(player, Packet.newBuilder().setCloseTradePacket(CloseTradeWindowPacket.newBuilder()
                .setWhich(2)
                .build()).build());
    }
}

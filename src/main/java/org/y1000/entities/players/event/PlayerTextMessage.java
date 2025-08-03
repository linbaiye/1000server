package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TextMessagePacket;

public class PlayerTextMessage extends Abstract2PlayerMessageEvent {

    private PlayerTextMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerTextMessage bottom(Player player, String text) {
        return new PlayerTextMessage(player, Packet.newBuilder().setText(TextMessagePacket.newBuilder().setText(text).build()).build());
    }

    public static PlayerTextMessage leftUp(Player player, String text) {
        return new PlayerTextMessage(player, Packet.newBuilder()
                .setText(TextMessagePacket.newBuilder()
                        .setLocation(2)
                        .setText(text)
                        .build()).build());
    }

    public static PlayerTextMessage left(Player player, String text) {
        return new PlayerTextMessage(player, Packet.newBuilder()
                .setText(TextMessagePacket.newBuilder()
                        .setLocation(1)
                        .setText(text)
                        .build()).build());
    }

    public static PlayerTextMessage systip(Player player, String text) {
        return new PlayerTextMessage(player, Packet.newBuilder().setText(TextMessagePacket.newBuilder().setText(text).build()).build());
    }
}

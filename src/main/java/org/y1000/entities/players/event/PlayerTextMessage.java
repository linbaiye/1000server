package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TextMessagePacket;

public class PlayerTextMessage extends Abstract2PlayerMessageEvent {

    private PlayerTextMessage(Player player, Packet packet) {
        super(player, packet);
    }

    private static final String PrivateChatColor = "#e139b2";
    public static PlayerTextMessage bottom(Player player, String text) {
        return new PlayerTextMessage(player, Packet.newBuilder().setText(TextMessagePacket.newBuilder()
                        .setLocation(0)
                .setText(text).build()).build());
    }

    public static PlayerTextMessage privateChat(Player player, String text) {
        return bottom(player, text, PrivateChatColor, "");
    }

    public static PlayerTextMessage bottom(Player player, String text, String color, String bgColor) {
        return new PlayerTextMessage(player, Packet.newBuilder().setText(TextMessagePacket.newBuilder().setText(text)
                        .setLocation(0)
                        .setColor(color)
                        .setBgColor(bgColor)
                .build()).build());
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
        return bottom(player, text, "yellow", "");
    }
}

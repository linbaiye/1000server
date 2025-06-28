package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TextMessagePacket;

public class PlayerTextMessage extends Abstract2ClientEntityEvent implements I2ClientMessage {
    private final Packet packet;

    private PlayerTextMessage(Player player, Packet packet) {
        super(player);
        this.packet = packet;
    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }

    public static PlayerTextMessage of(Player player, String text) {
        return new PlayerTextMessage(player, Packet.newBuilder().setText(TextMessagePacket.newBuilder().setText(text).build()).build());
    }
}

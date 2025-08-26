package org.y1000.entities.players.event;

import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowCreateGuildWindowPacket;

public class ShowCreateGuildWindowMessage implements I2ClientMessage {
    private final Packet packet;

    public ShowCreateGuildWindowMessage(Packet packet) {
        this.packet = packet;
    }

    @Override
    public Packet toPacket() {
        return packet;
    }

    public static ShowCreateGuildWindowMessage show(long id, int slotId) {
        ShowCreateGuildWindowPacket showCreateGuildWindowPacket = ShowCreateGuildWindowPacket
                .newBuilder()
                .setFromSlot(slotId)
                .setTile("创立门派")
                .setTip("输入门派名称")
                .build();
        Packet packet = Packet.newBuilder().setShowCreateGuild(showCreateGuildWindowPacket).build();
        return new ShowCreateGuildWindowMessage(packet);
    }
}

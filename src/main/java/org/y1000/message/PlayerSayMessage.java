package org.y1000.message;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.CreatureSayPacket;
import org.y1000.network.gen.Packet;

public final class PlayerSayMessage extends AbstractInsightPlayerMessage {

    private PlayerSayMessage(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerSayMessage say(Player player, String text) {
        Validate.notNull(player);
        Validate.notEmpty(text);
        return new PlayerSayMessage(player, Packet.newBuilder().setSay(CreatureSayPacket.newBuilder()
                        .setId(player.id())
                .setText(text).build()).build());
    }
}

package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.CreatureSayPacket;
import org.y1000.network.gen.Packet;

public final class PlayerSayEvent extends Abstract2VisibleAndSelfMessageEvent {

    private PlayerSayEvent(Player player, Packet packet) {
        super(player, packet);
    }


    private static PlayerSayEvent of(Player player, String text, boolean cache) {
        Validate.notNull(player);
        Validate.notEmpty(text);
        return new PlayerSayEvent(player, Packet.newBuilder().setSay(CreatureSayPacket.newBuilder()
                .setId(player.id())
                .setCache(cache)
                .setViewName(player.viewName())
                .setText(text).build()).build());
    }

    public static PlayerSayEvent kungfuTip(Player player, String text) {
        return of(player, text, false);
    }

    public static PlayerSayEvent say(Player player, String text) {
        return of(player, text, true);
    }
}

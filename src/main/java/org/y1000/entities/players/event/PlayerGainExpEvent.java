package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFu;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerGainExpPacket;

public final class PlayerGainExpEvent extends Abstract2PlayerMessageEvent {


    public PlayerGainExpEvent(Player player, Packet packet) {
        super(player, packet);
    }

    private static Packet buildPacket(boolean kungFu, String name, int level) {
        return Packet.newBuilder()
                .setGainExp(PlayerGainExpPacket.newBuilder()
                        .setKungFu(kungFu)
                        .setName(name)
                        .setLevel(level)
                        .build())
                .build();
    }

    public static PlayerGainExpEvent of(Player player, KungFu kungFu) {
        return new PlayerGainExpEvent(player, buildPacket(true, kungFu.name(), kungFu.level()));
    }

    public static PlayerGainExpEvent nonKungFu(Player player, String name) {
        return new PlayerGainExpEvent(player, buildPacket(false, name, 0));
    }
}

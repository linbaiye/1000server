package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerDamagedPacket;

public class PlayerDamagedEvent extends Abstract2PlayerMessageEvent {

    public PlayerDamagedEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerDamagedEvent create(Player player) {
        PlayerDamagedPacket damagedPacket = PlayerDamagedPacket.newBuilder()
                .setCurLife(player.currentLife())
                .setId(player.id())
                .setMaxLife(player.maxLife())
                .setLegPercent(player.legPercent())
                .setArmPercent(player.armPercent())
                .setHeadPercent(player.headPercent())
                .build();
        return new PlayerDamagedEvent(player, Packet.newBuilder().setPlayerDamaged(damagedPacket).build());
    }
}

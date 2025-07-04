package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.JoinRealmPacket;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerEquipPacket;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerJoinRealmMessage implements I2ClientMessage {

    private final Packet packet;

    public PlayerJoinRealmMessage(Packet packet) {
        this.packet = packet;
    }

    public static PlayerJoinRealmMessage of(Player player) {
        List<PlayerEquipPacket> equipments = player.getEquipments().stream().map(e -> PlayerEquipEvent.toEquipPacket(player, e)).collect(Collectors.toList());
        JoinRealmPacket joinRealmPacket = JoinRealmPacket.newBuilder()
                .setAttribute(PlayerAttributeEvent.makeAttributePacket(player))
                .setTeleport(PlayerTeleportEvent.teleportPacket(player.getRealm(), player.coordinate()))
                .setAttackKungFu(player.attackKungFu().name())
                .setId(player.id())
                .setName(player.viewName())
                .setMale(player.isMale())
                .addAllEquipments(equipments)
                .build();
        return new PlayerJoinRealmMessage(Packet.newBuilder().setJoinRealm(joinRealmPacket).build());
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}

package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.AbstractPlayerMessage;
import org.y1000.network.gen.JoinRealmPacket;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerEquipPacket;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerJoinRealmMessage extends AbstractPlayerMessage  {

    public PlayerJoinRealmMessage(Player source, Packet packet) {
        super(source, packet);
    }

    public static PlayerJoinRealmMessage of(Player player) {
        List<PlayerEquipPacket> equipments = player.getEquipments().stream().map(e -> PlayerEquipMessage.toEquipPacket(player, e)).collect(Collectors.toList());
        JoinRealmPacket joinRealmPacket = JoinRealmPacket.newBuilder()
                .setAttribute(PlayerAttributeMessage.makeAttributePacket(player))
                .setTeleport(PlayerTeleportEvent.teleportPacket(player.getRealm(), player.coordinate()))
                .setAttackKungFu(player.attackKungFu().name())
                .setId(player.id())
                .setName(player.viewName())
                .setMale(player.isMale())
                .addAllEquipments(equipments)
                .build();
        return new PlayerJoinRealmMessage(player, Packet.newBuilder().setJoinRealm(joinRealmPacket).build());
    }
}

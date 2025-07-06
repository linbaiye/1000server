package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.item.EquipmentType;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerUnequipPacket;

public final class PlayerUnequipEvent extends Abstract2VisibleAndSelfMessageEvent {


    public PlayerUnequipEvent(Player source,
                              Packet packet) {
        super(source, packet);
    }

    public static PlayerUnequipEvent of(Player player, EquipmentType type) {
        PlayerUnequipPacket.Builder builder = PlayerUnequipPacket.newBuilder()
                .setId(player.id())
                .setEquipmentType(type.value());
        Packet packet = Packet.newBuilder()
                .setUnequip(builder)
                .build();
        return new PlayerUnequipEvent(player, packet);
    }
}

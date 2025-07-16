package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.EntitySoundPacket;
import org.y1000.network.gen.Packet;

public final class PlayerSoundEvent extends Abstract2VisibleAndSelfMessageEvent {

    public PlayerSoundEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerSoundEvent sound(Player player, String sound) {
        EntitySoundPacket packet = EntitySoundPacket.newBuilder()
                .setEntityName(player.viewName())
                .setSound(sound).build();
        return new PlayerSoundEvent(player, Packet.newBuilder().setEntitySound(packet).build());
    }

}

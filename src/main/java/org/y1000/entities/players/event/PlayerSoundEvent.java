package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.EntitySoundPacket;
import org.y1000.network.gen.Packet;
import org.y1000.realm.PlayerEventHandler;

public final class PlayerSoundEvent extends AbstractMessagePlayerEvent {

    private final boolean toAll;

    private PlayerSoundEvent(Player player, Packet packet, boolean toAll) {
        super(player, packet);
        this.toAll = toAll;
    }


    public static PlayerSoundEvent toAll(Player player, String sound) {
        EntitySoundPacket packet = EntitySoundPacket.newBuilder()
                .setSound(sound).build();
        return new PlayerSoundEvent(player, Packet.newBuilder().setEntitySound(packet).build(), true);
    }

    public static PlayerSoundEvent toSelf(Player player, String sound) {
        EntitySoundPacket packet = EntitySoundPacket.newBuilder()
                .setSound(sound).build();
        return new PlayerSoundEvent(player, Packet.newBuilder().setEntitySound(packet).build(), false);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        if (toAll)
            handler.sendToVisiblePlayersAndSelf(source(), this);
        else
            handler.sendTo(source(), this);
    }

}

package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.message.serverevent.Visibility;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateGuildPacket;

public class PlayerUpdateGuildEvent extends AbstractPlayerEvent {

    private final Packet packet;

    public PlayerUpdateGuildEvent(Player source) {
        super(source, Visibility.VISIBLE_PLAYERS);
        if (source.guildMembership().isPresent()) {
            packet = Packet.newBuilder()
                    .setUpdateGuild(UpdateGuildPacket.newBuilder().setId(player().id()).setName(source.guildMembership().get().guildName()))
                    .build();
        } else {
            packet = Packet.newBuilder()
                    .setUpdateGuild(UpdateGuildPacket.newBuilder().setId(player().id()).setName(""))
                    .build();
        }
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}

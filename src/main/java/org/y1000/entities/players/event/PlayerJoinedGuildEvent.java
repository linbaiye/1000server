package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.JoinGuildPacket;
import org.y1000.network.gen.Packet;

public class PlayerJoinedGuildEvent extends AbstractPlayerEvent {

    private final Packet packet;

    public PlayerJoinedGuildEvent(Player source) {
        super(source, Visibility.VISIBLE_PLAYERS);
        Validate.isTrue(source.guildMembership().isPresent());
        packet = Packet.newBuilder()
                .setJoinGuild(JoinGuildPacket.newBuilder().setId(player().id()).setName(source.guildMembership().get().guildName()))
                .build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}

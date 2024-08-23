package org.y1000.entities.players.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.ChatPacket;
import org.y1000.network.gen.Packet;

public final class PlayerSpeakEvent extends AbstractPlayerEvent {

    private final Packet packet;
    public PlayerSpeakEvent(Player source, String content) {
        super(source);
        Validate.notNull(content);
        packet = Packet.newBuilder()
                .setChat(ChatPacket.newBuilder().setId(player().id())
                        .setContent(content)).build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}

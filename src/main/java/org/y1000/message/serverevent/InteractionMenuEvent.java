package org.y1000.message.serverevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.network.gen.NpcInteractionMenuPacket;
import org.y1000.network.gen.Packet;

import java.util.List;

public final class InteractionMenuEvent extends AbstractPlayerEvent {

    private final Packet packet;

    public InteractionMenuEvent(Player source,
                                String npcName,
                                long npcId,
                                String shape,
                                int avatar,
                                String text,
                                List<String> actions) {
        super(source, Visibility.SELF);
        Validate.notNull(npcName);
        Validate.notNull(shape);
        Validate.notNull(text);
        Validate.notNull(actions);
        packet = Packet.newBuilder()
                .setInteractionMenu(NpcInteractionMenuPacket.newBuilder()
                        .setId(npcId)
                        .setText(text)
                        .setShape(shape)
                        .setAvatarIdx(avatar)
                        .setViewName(npcName)
                        .addAllInteractions(actions)
                ).build();
    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}

package org.y1000.message.serverevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.event.Npc2ClientEvent;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.ChatPacket;
import org.y1000.network.gen.Packet;

public final class EntityChatEvent extends Abstract2ClientEntityEvent implements Npc2ClientEvent {

    private final Packet packet;


    public EntityChatEvent(ActiveEntity entity, String content) {
        super(entity);
        Validate.notNull(content);
        ChatPacket.Builder builder = ChatPacket.newBuilder()
                .setId(entity.id())
                .setContent(content);
        if (entity instanceof Player player) {
            builder.setFromPlayer(player.viewName());
        }
        packet = Packet.newBuilder()
                .setChat(builder)
                .build();
    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }

    @Override
    public Visibility getVisibility() {
        return Visibility.VISIBLE_PLAYERS;
    }
}

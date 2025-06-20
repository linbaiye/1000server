package org.y1000.message.input.chat;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityChatEvent;

public record ClientChatEvent(String content) implements ClientInputTextEvent {

    public EntityChatEvent toPlayerEvent(Player source) {
        Validate.notNull(source);
        return new EntityChatEvent(source, source.viewName() + "：" + content);
    }

    @Override
    public boolean canSend(Player player) {
        return player != null;
    }
}

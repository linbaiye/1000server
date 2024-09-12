package org.y1000.message.clientevent.chat;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.entities.players.event.PlayerSpeakEvent;

public record ClientSayEvent(String content) implements ClientChatEvent {

    public AbstractPlayerEvent toPlayerEvent(Player source) {
        Validate.notNull(source);
        return new PlayerSpeakEvent(source, source.viewName() + "：" + content);
    }

    @Override
    public boolean canSend(Player player) {
        return player != null;
    }
}

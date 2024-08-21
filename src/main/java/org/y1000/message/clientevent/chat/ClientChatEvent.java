package org.y1000.message.clientevent.chat;

import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientEvent;

public interface ClientChatEvent extends ClientEvent {

    boolean canSend(Player player);

    static ClientEvent create(String content) {
        if (ClientDirectMessageEvent.isFormatCorrect(content)) {
            return ClientDirectMessageEvent.parse(content);
        }
        return new ClientWorldShoutEvent(content);
    }
}

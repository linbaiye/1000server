package org.y1000.message.clientevent.chat;

import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientEvent;

public interface ClientChatEvent extends ClientEvent {

    boolean canSend(Player player);

    static ClientEvent create(String content) {
        if (ClientPrivateChatEvent.isFormatCorrect(content)) {
            return ClientPrivateChatEvent.parse(content);
        } else if (ClientWorldShoutEvent.isFormatCorrect(content)) {
            return ClientWorldShoutEvent.parse(content);
        }
        return new ClientSpeakEvent(content);
    }
}

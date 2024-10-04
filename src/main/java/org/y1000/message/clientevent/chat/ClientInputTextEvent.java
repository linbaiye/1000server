package org.y1000.message.clientevent.chat;

import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientEvent;

public interface ClientInputTextEvent extends ClientEvent {

    boolean canSend(Player player);

    static ClientEvent create(String content) {
        if (ClientWhisperEvent.isFormatCorrect(content)) {
            return ClientWhisperEvent.parse(content);
        } else if (ClientWorldShoutEvent.isFormatCorrect(content)) {
            return ClientWorldShoutEvent.parse(content);
        }
        return new ClientChatEvent(content);
    }
}

package org.y1000.message.clientevent;

import org.y1000.entities.players.Player;
import org.y1000.message.ClientDirectMessageEvent;

public interface ClientTextEvent extends ClientEvent {

    void handle(Player player);

    static ClientEvent create(String content) {
        if (ClientDirectMessageEvent.isFormatCorrect(content)) {
            return ClientDirectMessageEvent.parse(content);
        }
        return new ClientWorldSayEvent(content);
    }
}

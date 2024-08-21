package org.y1000.message.clientevent.chat;

import org.y1000.entities.players.Player;

public record ClientSpeakEvent(String content) implements ClientChatEvent {
    public void handle(Player source) {
        if (source != null)
            return;
    }

    @Override
    public boolean canSend(Player player) {
        return false;
    }
}

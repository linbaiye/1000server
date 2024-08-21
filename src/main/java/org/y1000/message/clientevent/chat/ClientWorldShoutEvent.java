package org.y1000.message.clientevent.chat;

import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.realm.event.BroadcastChatEvent;
import org.y1000.realm.event.RealmEvent;

public record ClientWorldShoutEvent(String content) implements ClientRealmChatEvent {

    @Override
    public RealmEvent toRealmEvent(Player player) {
        return new BroadcastChatEvent(player.viewName() + ":" + content, PlayerTextEvent.TextType.CUSTOM,
                PlayerTextEvent.ColorType.EIGHT_GRADE);
    }

    @Override
    public boolean canSend(Player player) {
        return player != null && player.stateEnum() != State.DIE;
    }
}

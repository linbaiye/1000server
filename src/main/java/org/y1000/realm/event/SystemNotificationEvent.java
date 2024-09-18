package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;

public final class SystemNotificationEvent implements BroadcastEvent {

    private final String text;

    public SystemNotificationEvent(String text) {
        this.text = text;
    }

    @Override
    public void send(Player player) {
        player.emitEvent(PlayerTextEvent.systemNotification(player, text));
    }
}

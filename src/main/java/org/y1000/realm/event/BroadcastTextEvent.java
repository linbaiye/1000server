package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;

public final class BroadcastTextEvent implements RealmEvent {

    private BroadcastTextEvent(Location location, String text) {
        this.location = location;
        this.text = text;
    }

    private enum Location {
        LeftUp,
        Bottom
    }

    private final Location location;

    private final String text;

    public PlayerTextMessage createMessage(Player player) {
        if (location == Location.LeftUp)
            return PlayerTextMessage.leftUp(player, text);
        else
            return PlayerTextMessage.bottom(player, text);
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.broadcastText(this);
    }

    public static BroadcastTextEvent leftUp(String text) {
        return new BroadcastTextEvent(Location.LeftUp, text);
    }
}

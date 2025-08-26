package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;

public final class BroadcastTextEvent implements RealmEvent {

    private BroadcastTextEvent(Location location, String text, String color, String bgColor) {
        this.location = location;
        this.text = text;
        this.color = color;
        this.bgColor = bgColor;
    }

    private enum Location {
        LeftUp,
        Bottom
    }

    private final Location location;

    private final String text;

    private final String color;

    private final String bgColor;

    public PlayerTextMessage createMessage(Player player) {
        if (location == Location.LeftUp)
            return PlayerTextMessage.leftUp(player, text);
        else
            return PlayerTextMessage.bottom(player, text, color, bgColor);
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.broadcastText(this);
    }

    public static BroadcastTextEvent bottom(String text, String color, String bgColor) {
        return new BroadcastTextEvent(Location.Bottom, text, color, bgColor);
    }

    public static BroadcastTextEvent leftUp(String text) {
        return new BroadcastTextEvent(Location.LeftUp, text, null, null);
    }
}

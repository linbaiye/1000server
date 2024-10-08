package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.TextMessage;

public final class BroadcastTextEvent extends AbstractBroadcastTextEvent {

    public BroadcastTextEvent(String text,
                              TextMessage.TextType textType,
                              TextMessage.ColorType colorType) {
        this(text, textType, colorType, TextMessage.Location.DOWN);
    }

    public BroadcastTextEvent(String text,
                              TextMessage.TextType textType,
                              TextMessage.ColorType colorType,
                              TextMessage.Location location) {
        super(text, textType, colorType, location);
    }

    @Override
    public void send(Player player) {
        if (player != null)
            player.emitEvent(new PlayerTextEvent(player, text, textType, location, colorType));
    }

    public static BroadcastTextEvent leftUp(String text) {
        return new BroadcastTextEvent(text, TextMessage.TextType.CUSTOM, TextMessage.ColorType.SAY, TextMessage.Location.LEFT_UP);
    }

}

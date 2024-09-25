package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.TextMessage;

public final class BroadcastTextEvent implements BroadcastEvent {

    private final String text;
    private final TextMessage.TextType textType;
    private final TextMessage.ColorType colorType;

    private final TextMessage.Location location;

    public BroadcastTextEvent(String text,
                              TextMessage.TextType textType,
                              TextMessage.ColorType colorType) {
        this(text, textType, colorType, TextMessage.Location.DOWN);
    }

    public BroadcastTextEvent(String text,
                              TextMessage.TextType textType,
                              TextMessage.ColorType colorType,
                              TextMessage.Location location) {
        this.text = text;
        this.textType = textType;
        this.colorType = colorType;
        this.location = location;
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

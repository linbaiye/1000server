package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;

public final class BroadcastTextEvent implements BroadcastEvent {

    private final String text;
    private final PlayerTextEvent.TextType textType;
    private final PlayerTextEvent.ColorType colorType;

    public BroadcastTextEvent(String text,
                              PlayerTextEvent.TextType textType,
                              PlayerTextEvent.ColorType colorType) {
        this.text = text;
        this.textType = textType;
        this.colorType = colorType;
    }

    @Override
    public void send(Player player) {
        if (player != null)
            player.emitEvent(new PlayerTextEvent(player, text, textType, PlayerTextEvent.Location.DOWN, colorType));
    }
}

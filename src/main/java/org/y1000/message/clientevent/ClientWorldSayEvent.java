package org.y1000.message.clientevent;

import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.realm.event.BroadcastEvent;

public record ClientWorldSayEvent(String content) implements ClientEvent, BroadcastEvent {

    @Override
    public boolean withinRealm() {
        return false;
    }

    @Override
    public void send(Player player) {
        player.emitEvent(new PlayerTextEvent(player, content, PlayerTextEvent.TextType.CUSTOM, PlayerTextEvent.Location.DOWN, PlayerTextEvent.ColorType.NINE_GRADE));
    }
}

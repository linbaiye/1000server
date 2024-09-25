package org.y1000.message.clientevent;

import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.TextMessage;
import org.y1000.realm.event.BroadcastEvent;

public record ClientWorldSayEvent(String content) implements ClientEvent, BroadcastEvent {


    @Override
    public void send(Player player) {
        player.emitEvent(new PlayerTextEvent(player, content, TextMessage.TextType.CUSTOM, TextMessage.Location.DOWN, TextMessage.ColorType.NINE_GRADE));
    }
}

package org.y1000.message.input.chat;

import org.y1000.entities.players.Player;
import org.y1000.realm.event.IRealmEvent;

public interface ClientRealmChatEvent extends ClientInputTextEvent {

    IRealmEvent toRealmEvent(Player player);
}

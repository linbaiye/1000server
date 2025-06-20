package org.y1000.message.input.chat;

import org.y1000.entities.players.Player;
import org.y1000.realm.event.RealmEvent;

public interface ClientRealmChatEvent extends ClientInputTextEvent {

    RealmEvent toRealmEvent(Player player);
}

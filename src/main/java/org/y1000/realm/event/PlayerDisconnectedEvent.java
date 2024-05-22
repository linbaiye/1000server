package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public record PlayerDisconnectedEvent(Player player) implements RealmEvent {

}

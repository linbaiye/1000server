package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public record PlayerDisconnectedEvent(int realmId, Player player) implements PlayerRealmEvent {

}

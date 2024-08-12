package org.y1000.realm.event;


import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientEvent;

public record PlayerDataEvent(int realmId, Player player, ClientEvent data) implements PlayerRealmEvent {

}

package org.y1000.realm.event;


import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientEvent;

public record PlayerDataEvent(int toRealmId, @Deprecated Player player, ClientEvent data) implements PlayerRealmEvent {

    public long playerId() {
        return player.id();
    }

}

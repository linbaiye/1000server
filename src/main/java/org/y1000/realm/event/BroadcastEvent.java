package org.y1000.realm.event;


import org.y1000.entities.players.Player;

public interface BroadcastEvent extends IRealmEvent {

    void send(Player player);
}

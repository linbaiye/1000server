package org.y1000.realm.event;


import org.y1000.entities.players.Player;

public interface BroadcastEvent extends RealmEvent {

    @Override
    default RealmEventType realmEventType() {
        return RealmEventType.BROADCAST;
    }

    void send(Player player);
}

package org.y1000.realm.event;

import org.y1000.entities.players.Player;

public interface PlayerRealmEvent extends RealmEvent {

    Player player();

    int realmId();

    default RealmEventType realmEventType() {
        return RealmEventType.IDENTIFIED;
    }
}

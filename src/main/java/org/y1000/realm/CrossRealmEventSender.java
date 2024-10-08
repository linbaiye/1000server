package org.y1000.realm;

import org.y1000.realm.event.RealmEvent;

public interface CrossRealmEventSender {
    void send(RealmEvent realmEvent);
}

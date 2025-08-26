package org.y1000.realm;

import org.y1000.realm.event.RealmEvent;

public interface RealmEventSender {
    void send(RealmEvent realmEvent);
}

package org.y1000.realm;

import org.y1000.realm.event.RealmEvent;

public interface RealmEventHandler {
    void handle(RealmEvent realmEvent);
}

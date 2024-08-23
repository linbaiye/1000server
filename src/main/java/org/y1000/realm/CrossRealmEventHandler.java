package org.y1000.realm;

import org.y1000.realm.event.RealmEvent;

public interface CrossRealmEventHandler {
    void handle(RealmEvent realmEvent);
}

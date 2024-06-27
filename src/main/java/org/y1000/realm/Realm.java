package org.y1000.realm;

import org.y1000.realm.event.RealmEvent;


public interface Realm {
    int STEP_MILLIS = 10;

    void handle(RealmEvent event);

    RealmMap map();
    default String name() {
        return map().name();
    }
}

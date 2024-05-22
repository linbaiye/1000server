package org.y1000.realm;

import org.y1000.entities.PhysicalEntity;
import org.y1000.network.event.ConnectionEvent;
import org.y1000.realm.event.RealmEvent;

import java.util.Optional;

public interface Realm {
    int STEP_MILLIS = 10;

    Optional<PhysicalEntity> findInsight(PhysicalEntity source, long id);

    void handle(RealmEvent event);

    RealmMap map();
    default String name() {
        return map().name();
    }
}

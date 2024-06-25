package org.y1000.realm;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Entity;
import org.y1000.realm.event.RealmEvent;

import java.util.Optional;

public interface Realm {
    int STEP_MILLIS = 10;

    Optional<Entity> findInsight(Entity source, long id);


    void handle(RealmEvent event);

    RealmMap map();
    default String name() {
        return map().name();
    }
}

package org.y1000.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.Entity;
import org.y1000.realm.event.RealmEvent;

public record CrossRealmEvent(Entity source, RealmEvent realmEvent) implements EntityEvent {
    public CrossRealmEvent {
        Validate.notNull(source);
        Validate.notNull(realmEvent);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }
}

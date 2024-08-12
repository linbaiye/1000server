package org.y1000.entities.objects;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.Entity;
import org.y1000.event.CrossRealmEvent;
import org.y1000.event.EntityEventVisitor;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.RealmLetterEvent;

public final class EntityCrossRealmEvent implements CrossRealmEvent {
    private final Entity source;
    private final int realmId;

    public EntityCrossRealmEvent(Entity source, int id) {
        Validate.notNull(source);
        this.source = source;
        this.realmId = id;
    }

    @Override
    public Entity source() {
        return source;
    }

    public RealmEvent toRealmEvent() {
        return new RealmLetterEvent<>(realmId, "九尾狐酒母", "shift");
    }
    @Override
    public void accept(EntityEventVisitor visitor) {

    }
}

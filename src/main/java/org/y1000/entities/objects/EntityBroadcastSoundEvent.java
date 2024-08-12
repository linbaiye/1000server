package org.y1000.entities.objects;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.Entity;
import org.y1000.event.CrossRealmEvent;
import org.y1000.event.EntityEventVisitor;
import org.y1000.realm.event.BroadcastSoundEvent;

public final class EntityBroadcastSoundEvent implements CrossRealmEvent {
    private final Entity entity;
    private final String sound;

    public EntityBroadcastSoundEvent(Entity entity,
                                     String s) {
        Validate.notNull(entity);
        Validate.notNull(s);
        this.entity = entity;
        sound = s;
    }

    @Override
    public Entity source() {
        return entity;
    }

    public BroadcastSoundEvent toRealmEvent() {
        return new BroadcastSoundEvent(sound);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }
}

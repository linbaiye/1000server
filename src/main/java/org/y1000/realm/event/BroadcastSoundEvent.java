package org.y1000.realm.event;


import lombok.Getter;
import org.apache.commons.lang3.Validate;

public record BroadcastSoundEvent(String sound) implements RealmEvent {

    public BroadcastSoundEvent {
        Validate.notNull(sound);
    }

    @Override
    public RealmEventType realmEventType() {
        return RealmEventType.BROADCAST;
    }
}

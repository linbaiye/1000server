package org.y1000.realm.event;

public record RealmTriggerEvent(int realmId, String toName) implements RealmEvent {

    @Override
    public RealmEventType realmEventType() {
        return RealmEventType.IDENTIFIED;
    }
}

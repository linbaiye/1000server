package org.y1000.realm.event;

public record RealmLetterEvent<T>(int realmId, String toName, T content) implements RealmEvent {

    @Override
    public RealmEventType realmEventType() {
        return RealmEventType.IDENTIFIED;
    }
}

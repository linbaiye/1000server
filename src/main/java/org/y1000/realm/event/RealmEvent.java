package org.y1000.realm.event;

public interface RealmEvent {
    int toRealm();
    void accept(RealmEventHandler  handler);
}

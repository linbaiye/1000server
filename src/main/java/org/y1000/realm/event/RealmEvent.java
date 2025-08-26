package org.y1000.realm.event;

public interface RealmEvent {
    void accept(RealmEventHandler handler);
}

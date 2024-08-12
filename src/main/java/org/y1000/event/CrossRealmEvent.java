package org.y1000.event;

import org.y1000.realm.event.RealmEvent;

public interface CrossRealmEvent extends EntityEvent {
    RealmEvent toRealmEvent();
}

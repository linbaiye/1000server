package org.y1000.realm.event;

import org.y1000.network.Connection;

public interface PlayerConnectedEvent extends RealmEvent {

    Connection connection();


}

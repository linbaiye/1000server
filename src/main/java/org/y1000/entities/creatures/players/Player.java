package org.y1000.entities.creatures.players;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.Message;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.List;

public interface Player extends Creature {

    List<Message> handle(List<Message> messages);

    static Player ofRealm(Realm realm) {
        return new PlayerImpl(realm);
    }

    static Player ofRealm(Realm realm, Coordinate coordinate) {
        return new PlayerImpl(realm, coordinate);
    }
}

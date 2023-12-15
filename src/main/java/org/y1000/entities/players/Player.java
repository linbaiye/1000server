package org.y1000.entities.players;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.Message;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.List;

public interface Player extends Creature {

    List<I2ClientMessage> handle(List<Message> messages);

    State state();

    static Player ofRealm(Realm realm, Coordinate coordinate) {
        return new PlayerImpl(realm, coordinate, 0, 0);
    }

    static Player ofRealm(Realm realm, Coordinate coordinate, long id, long joinedAtMillis) {
        return new PlayerImpl(realm, coordinate, id, joinedAtMillis);
    }

    Interpolation snapshot();

    long joinedAtMilli();
}

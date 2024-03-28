package org.y1000.entities.players;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.input.InputMessage;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.List;

public interface Player extends Creature {

    List<I2ClientMessage> handle(List<InputMessage> messages);

    State state();

    void joinReam(Realm realm, long joinedAtMillis);

    static Player create(long id, Coordinate coordinate) {
        return new PlayerImpl(id, coordinate);
    }

    long interpolationDuration();

    List<Interpolation> drainInterpolations(long durationMillis);

    long joinedAtMilli();
}

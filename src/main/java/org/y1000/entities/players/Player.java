package org.y1000.entities.players;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.*;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.List;

public interface Player extends Creature {

    void addAll(List<ClientEvent> clientEvents);

    State state();

    default boolean isMale() {
        return true;
    }

    void joinReam(Realm realm);

    void registerListener(ServerEventListener listener);

    static Player create(long id, Coordinate coordinate) {
        return new PlayerImpl(id, coordinate);
    }
}

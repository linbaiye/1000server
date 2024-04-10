package org.y1000.entities.players;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.EntityEvent;
import org.y1000.message.ServerEvent;
import org.y1000.message.PlayerInterpolation;
import org.y1000.message.ServerEventListener;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.List;

public interface Player extends Creature {

    List<ServerEvent> handle(List<ClientEvent> messages);

    void addAll(List<ClientEvent> clientEvents);

    State state();

    void joinReam(Realm realm);

    PlayerInterpolation captureInterpolation();

    void registerListener(ServerEventListener<EntityEvent> listener);

    static Player create(long id, Coordinate coordinate) {
        return new PlayerImpl(id, coordinate);
    }
}

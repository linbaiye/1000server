package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.magic.FootMagic;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Optional;

public interface Player extends Creature {

    void addAll(List<ClientEvent> clientEvents);

    State stateEnum();

    default boolean isMale() {
        return true;
    }

    void joinReam(RealmMap realm);

    void leaveRealm();

    default Optional<FootMagic> footMagic() {
        return Optional.empty();
    }

    static Player create(long id, Coordinate coordinate) {
        return new PlayerImpl(id, coordinate, Direction.DOWN, "杨过");
    }
}

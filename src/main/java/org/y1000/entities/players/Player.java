package org.y1000.entities.players;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.equipment.weapon.Weapon;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.network.Connection;
import org.y1000.entities.Direction;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.Optional;

public interface Player extends ViolentCreature {

    default boolean isMale() {
        return true;
    }

    Connection connection();

    void joinReam(Realm realm);

    Realm getRealm();

    void leaveRealm();

    default Optional<FootKungFu> footKungFu() {
        return Optional.empty();
    }

    default Optional<Weapon> weapon() {
        return Optional.empty();
    }

    AttackKungFu attackKungFu();

    static Player create(long id, Coordinate coordinate, Connection connection) {
        return new PlayerImpl(id, coordinate, Direction.DOWN, "杨过", connection);
    }
}

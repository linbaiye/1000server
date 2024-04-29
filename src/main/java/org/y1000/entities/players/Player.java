package org.y1000.entities.players;

import org.y1000.entities.players.equipment.weapon.Weapon;
import org.y1000.entities.players.kungfu.attack.AttackKungFu;
import org.y1000.network.Connection;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.realm.RealmImpl;
import org.y1000.util.Coordinate;

import java.util.Optional;

public interface Player extends Creature {


    default boolean isMale() {
        return true;
    }

    Connection connection();

    void joinReam(RealmImpl realm);


    void leaveRealm();

    default Optional<FootKungFu> footKungFu() {
        return Optional.empty();
    }

    default Optional<Weapon> weapon() {
        return Optional.empty();
    }

    default Optional<AttackKungFu> attackKungFu() {
        return Optional.empty();
    }

    static Player create(long id, Coordinate coordinate, Connection connection) {
        return new PlayerImpl(id, coordinate, Direction.DOWN, "杨过", connection);
    }
}

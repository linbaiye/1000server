package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.input.SelfHandleInput;
import org.y1000.network.Connection;
import org.y1000.network.I2ClientMessage;
import org.y1000.util.Coordinate;

import java.util.Optional;
import java.util.Set;

interface PlayerManager extends ActiveEntityManager<Player> {

    void loginPlayer(Player player, Realm realm, Coordinate coordinate, Connection connection);

    void loginPlayer(Player player, Realm realm, Connection connection);

    void logoutPlayer(Connection connection);

    void logoutPlayer(Player player);

    void teleportIn(Player player,
                    Realm realm,
                    Coordinate coordinate,
                    Connection connection);

    Connection prepareTeleport(Player player);

    Set<Player> allPlayers();

    void handleInput(Connection connection, SelfHandleInput input);

    Optional<Player> find(Connection connection);

    default void shutdown() {

    }

    void sendMessage(Player player, I2ClientMessage message);

}

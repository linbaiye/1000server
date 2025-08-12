package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.network.Connection;
import org.y1000.network.I2ClientMessage;

import java.util.*;

/**
 * Responsible for sending messages to players.
 */
@Slf4j
final class RealmPlayerConnectionManager implements MessageSender {

    private final Map<Player, Connection> playerConnectionMap = new HashMap<>(500);

    private final Map<Connection, Player> connectionPlayerMap = new HashMap<>(500);


    public void sendTo(Player player, I2ClientMessage serverMessage) {
        Validate.notNull(player);
        Validate.notNull(serverMessage);
        if (playerConnectionMap.containsKey(player))
            playerConnectionMap.get(player).writeAndFlush(serverMessage);
    }


    public void add(Player player, Connection connection) {
        Validate.notNull(player);
        Validate.notNull(connection);
        playerConnectionMap.put(player, connection);
        connectionPlayerMap.put(connection, player);
    }

    public Optional<Player> findPlayer(Connection connection) {
        return connection == null ? Optional.empty() :
                Optional.ofNullable(connectionPlayerMap.get(connection));
    }

    public Optional<Connection> remove(Player player) {
        if (player == null)
            return Optional.empty();
        Connection connection = playerConnectionMap.remove(player);
        if (connection != null)
            connectionPlayerMap.remove(connection);
        return Optional.ofNullable(connection);
    }
}

package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.*;
import org.y1000.entities.players.Player;
import org.y1000.message.*;
import org.y1000.network.Connection;

import java.util.*;

/**
 * Responsible for sending events to visible clients.
 */
@Slf4j
final class RealmPlayerConnectionManager implements MessageSender {

    private final AOIManager scopeManager;

    private final Map<Player, Connection> playerConnectionMap = new HashMap<>(500);
    private final Map<Connection, Player> connectionPlayerMap = new HashMap<>(500);

    public RealmPlayerConnectionManager(AOIManager scopeManager) {
        Validate.notNull(scopeManager);
        this.scopeManager = scopeManager;
    }

    private void notifyInterpolation(Player joined, Entity entity) {
        sendTo(joined, entity.captureSnapshot());
        if (entity instanceof Player another) {
            sendTo(another, joined.captureSnapshot());
        }
    }

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
        scopeManager.remove(player);
        Connection connection = playerConnectionMap.remove(player);
        if (connection != null)
            connectionPlayerMap.remove(connection);
        return Optional.ofNullable(connection);
    }

    public Optional<Connection> findConnection(Player player) {
        return player == null ? Optional.empty() : Optional.ofNullable(playerConnectionMap.get(player));
    }

}

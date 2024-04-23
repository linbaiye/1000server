package org.y1000.connection;

import org.y1000.entities.players.Player;
import org.y1000.entities.repository.PlayerRepository;
import org.y1000.message.clientevent.ClientEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionManagerImpl implements ConnectionManager,
        ConnectionEventListener {
    private final Map<Connection, Player> connectionPlayerMap;

    private final Map<Player, Connection> playerConnectionMap;

    private final Set<Player> joined;

    private final PlayerRepository playerRepository;

    private final Set<Player> left;

    private ConnectionManagerImpl(PlayerRepository playerRepository) {
        connectionPlayerMap = new HashMap<>();
        playerConnectionMap = new HashMap<>();
        joined = new HashSet<>();
        left = new HashSet<>();
        this.playerRepository = playerRepository;
    }


    @Override
    public Connection getConnection(Player player) {
        if (playerConnectionMap.containsKey(player)) {
            return playerConnectionMap.get(player);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void OnClosed(Connection connection) {
        Player removed;
        synchronized (this) {
            removed = connectionPlayerMap.remove(connection);
            if (removed != null) {
                playerConnectionMap.remove(removed);
                joined.removeIf(removed::equals);
                playerRepository.save(removed);
            }
        }
    }

    @Override
    public void OnEstablished(Connection connection) {
        Player player = playerRepository.load();
        synchronized (this) {
            connectionPlayerMap.put(connection, player);
            playerConnectionMap.put(player, connection);
            joined.add(player);
        }
    }


    @Override
    public void OnEventArrived(Connection connection, ClientEvent clientEvent) {
        if (!connectionPlayerMap.containsKey(connection)) {
            return;
        }
        OnEstablished(connection);
    }
}

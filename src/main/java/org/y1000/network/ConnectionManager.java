package org.y1000.network;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.players.Player;
import org.y1000.entities.repository.PlayerRepository;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.clientevent.LoginEvent;
import org.y1000.realm.RealmManager;

import java.util.*;

@Slf4j
public final class ConnectionManager {
    private final Map<Connection, Player> connectionPlayerMap;

    private final Set<Connection> pending;

    private final PlayerRepository playerRepository;

    private final RealmManager realmManager;

    public ConnectionManager(PlayerRepository playerRepository,
                             RealmManager realmManager) {
        Objects.requireNonNull(playerRepository);
        Objects.requireNonNull(realmManager);
        this.realmManager = realmManager;
        connectionPlayerMap = new HashMap<>();
        this.playerRepository = playerRepository;
        pending = new HashSet<>();
    }


    public void onClosed(Connection connection) {
        Objects.requireNonNull(connection);
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            playerRepository.save(removed);
            realmManager.onPlayerDisconnected(removed);
        }
    }

    public void onClientEvent(Connection connection, ClientEvent event) {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(event);
        if (!pending.contains(connection)) {
            return;
        }
        if (event instanceof LoginEvent) {
            pending.remove(connection);
            Player player = playerRepository.load(connection);
            log.info("Player {} logged in.", player);
            connectionPlayerMap.put(connection, player);
            realmManager.onPlayerConnected(player, "start");
        } else {
            connection.close();
        }
    }

    public void shutdown() {
        pending.forEach(Connection::close);
        connectionPlayerMap.values().forEach(playerRepository::save);
        connectionPlayerMap.keySet().forEach(Connection::close);
    }

    public void onEstablished(Connection connection) {
        Objects.requireNonNull(connection);
        pending.add(connection);
    }
}

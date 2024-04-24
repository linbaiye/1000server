package org.y1000.network;

import org.y1000.entities.players.Player;
import org.y1000.entities.repository.PlayerRepository;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.realm.RealmManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ConnectionManagerImpl implements ConnectionManager,
        ClientEventListener {
    private final Map<Connection, Player> connectionPlayerMap;

    private final Set<Connection> pending;

    private final PlayerRepository playerRepository;

    private final RealmManager realmManager;

    private ConnectionManagerImpl(PlayerRepository playerRepository,
                                  RealmManager realmManager) {
        this.realmManager = realmManager;
        connectionPlayerMap = new HashMap<>();
        this.playerRepository = playerRepository;
        pending = new HashSet<>();
    }


    @Override
    public void OnClosed(Connection connection) {
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            playerRepository.save(removed);
        }
        //pending.remove(connection);
    }

    @Override
    public void OnEstablished(Connection connection) {
        //pending.add(connection);
        //connection.registerClientEventListener();
        Player player = playerRepository.load();
        connectionPlayerMap.put(connection, player);
    }

    @Override
    public void OnEvent(ClientEvent clientEvent) {
        // handle login packet here.
    }
}

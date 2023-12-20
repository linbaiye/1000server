package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.connection.ConnectionEventListener;
import org.y1000.connection.ConnectionEventType;
import org.y1000.entities.managers.PlayerManager;
import org.y1000.entities.players.Player;
import org.y1000.entities.repository.PlayerRepository;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public class Realm implements Runnable, ConnectionEventListener  {

    private static final long stepMilli = 50;

    private long time;

    private final PlayerManager playerManager;

    private final List<Connection> closingConnections;

    private final Map<Connection, Player> joiningPlayers;

    private final PlayerRepository playerRepository;

    private final RealmMap realmMap;


    public Realm(PlayerRepository playerRepository, RealmMap map) {
        this.playerRepository = playerRepository;
        playerManager = new PlayerManager();
        realmMap = map;
        closingConnections = new ArrayList<>();
        joiningPlayers = new HashMap<>();
        time = 0;
    }

    public RealmMap map() {
        return realmMap;
    }

    public boolean hasPhysicalEntityAt(Coordinate coordinate) {
        return playerManager.findOne(coordinate) != null;
    }

    public boolean canMoveTo(Coordinate coordinate) {
        return map().movable(coordinate) && !hasPhysicalEntityAt(coordinate);
    }

    public void onConnectionEstablished(Connection connection) {
        long id = connection.id();
        Player player = playerRepository.load(id);
        synchronized (this) {
            joiningPlayers.put(connection, player);
        }
        notify();
    }

    public synchronized void onConnectionClosed(Connection connection) {
        closingConnections.add(connection);
        notify();
    }


    private void handleConnectionEvents() {
        List<Connection> deadConnections = Collections.emptyList();
        Map<Connection, Player> newPlayers = new HashMap<>();
        synchronized (this) {
            if (!closingConnections.isEmpty()) {
                deadConnections = new ArrayList<>(closingConnections);
                closingConnections.clear();
            }
            if (!joiningPlayers.isEmpty()) {
                newPlayers.putAll(joiningPlayers);
                joiningPlayers.clear();
            }
            notifyAll();
        }
        deadConnections.forEach(playerManager::remove);
        newPlayers.forEach((c, p) -> {
            p.joinReam(this, time);
            playerManager.add(c, p);
        });
    }

    @Override
    public void run() {
        try {
            long timeMillis = System.currentTimeMillis();
            while (true) {
                handleConnectionEvents();
                long current = System.currentTimeMillis();
                if (timeMillis <= current) {
                    playerManager.update(stepMilli, time);
                    timeMillis += stepMilli;
                    time += stepMilli;
                    playerManager.syncState();
                } else {
                    Thread.sleep(timeMillis - current);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public void OnEvent(ConnectionEventType type, Connection connection) {
        if (type == ConnectionEventType.ESTABLISHED) {
            onConnectionEstablished(connection);
        } else if (type == ConnectionEventType.CLOSED) {
            onConnectionClosed(connection);
        }
    }

    public static Optional<Realm> create(String name, PlayerRepository playerRepository) {
        Optional<RealmMap> mapOptional = RealmMap.Load(name);
        return mapOptional.map(map -> new Realm(playerRepository, map));
    }
}

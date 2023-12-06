package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.connection.ConnectionEventListener;
import org.y1000.connection.ConnectionEventType;
import org.y1000.entities.managers.PlayerManager;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public class Realm implements Runnable, ConnectionEventListener  {

    private long lastUpdateMilli;

    private static final long stepMilli = 50;

    private final PlayerManager playerManager;

    private final List<Connection> addingConnections;

    private final List<Connection> closingConnections;

    private final RealmMap realmMap;


    public Realm(RealmMap map) {
        lastUpdateMilli = 0;
        playerManager = new PlayerManager();
        realmMap = map;
        addingConnections = new ArrayList<>();
        closingConnections = new ArrayList<>();
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

    public synchronized void onConnectionEstablished(Connection connection) {
        addingConnections.add(connection);
        notify();
    }

    public synchronized void onConnectionClosed(Connection connection) {
        closingConnections.add(connection);
        notify();
    }

    private void updateRealm(long delta) {
        playerManager.update(delta);
    }


    private void handleConnectionEvents() {
        List<Connection> newConnections = Collections.emptyList();
        List<Connection> deadConnections = Collections.emptyList();
        synchronized (this) {
            if (!closingConnections.isEmpty()) {
                deadConnections = new ArrayList<>(closingConnections);
                closingConnections.clear();
            }
            if (!addingConnections.isEmpty()) {
                newConnections = new ArrayList<>(addingConnections);
                addingConnections.clear();
            }
            notifyAll();
        }
        deadConnections.forEach(playerManager::remove);
        newConnections.forEach(c -> playerManager.add(c, this));
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            long end = start;
            while (true) {
                Thread.sleep(Math.max(stepMilli - (end - start), 0));
                start = System.currentTimeMillis();
                updateRealm(stepMilli);
                handleConnectionEvents();
                end = System.currentTimeMillis();
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

    public static Optional<Realm> create(String name) {
        Optional<RealmMap> mapOptional = RealmMap.Load(name);
        return mapOptional.map(Realm::new);
    }
}

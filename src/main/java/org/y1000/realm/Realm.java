package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.connection.ConnectionEventListener;
import org.y1000.connection.ConnectionEventType;
import org.y1000.entities.managers.PlayerManager;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.function.UnaryOperator;

@Slf4j
public class Realm implements Runnable, ConnectionEventListener  {

    private long lastUpdateMilli;

    private static final long stepMilli = 50;

    private final PlayerManager playerManager;

    private final List<Connection> addingConnections;

    private final List<Connection> closingConnections;

    private final RealmMap realmMap;


    public Realm() {
        lastUpdateMilli = System.currentTimeMillis();
        playerManager = new PlayerManager();
        realmMap = new V2Map();
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

    private boolean hasConnectionEvents() {
        return !closingConnections.isEmpty() || !addingConnections.isEmpty();
    }

    private void updateRealm(long delta) {
        playerManager.update(delta);
    }

    @Override
    public void run() {
        try {
            while (true) {
                long currentMilli = System.currentTimeMillis();
                long nextWakeupMilli = stepMilli;
                long delta = 0;
                List<Connection> newConnections;
                List<Connection> deadConnections;
                synchronized (this) {
                    while (!hasConnectionEvents() && delta < stepMilli) {
                        wait(nextWakeupMilli);
                        delta = System.currentTimeMillis() - lastUpdateMilli;
                        nextWakeupMilli = stepMilli - delta;
                    }
                    deadConnections = new ArrayList<>(closingConnections);
                    newConnections = new ArrayList<>(addingConnections);
                    closingConnections.clear();
                    addingConnections.clear();
                    notifyAll();
                }
                deadConnections.forEach(playerManager::remove);
                newConnections.forEach(c -> playerManager.add(c, this));
                if (delta >= stepMilli) {
                    updateRealm(delta);
                    lastUpdateMilli = currentMilli;
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
}

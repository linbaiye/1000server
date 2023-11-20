package org.y1000.realm;

import org.y1000.connection.Connection;
import org.y1000.entities.EntityManager;
import org.y1000.entities.creatures.players.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Realm implements Runnable {
    private final Set<EntityManager<?>> entityManagers;

    private final Set<Connection> connectionSet = new HashSet<>();

    private final Set<Player> players = new HashSet<>();

    private long lastEpoch;

    private final List<Connection> addingConnections = new ArrayList<>();

    private final List<Connection> removingConnections = new ArrayList<>();

    public Realm(Set<EntityManager<?>> entityManagers) {
        this.entityManagers = entityManagers;
        lastEpoch = System.currentTimeMillis();
    }

    public synchronized void addNewConnection(Connection connection) {
        connectionSet.add(connection);
        notify();
    }

    public synchronized void onConnectionClosed(Connection connection) {
        removingConnections.add(connection);
        notify();
    }


    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                while (removingConnections.isEmpty() && addingConnections.isEmpty()) {
                    try {
                        wait(50);
                        if (!removingConnections.isEmpty())
                        {
                            removingConnections.forEach(connectionSet::remove);
                        }
                        if (!addingConnections.isEmpty())
                        {
                            connectionSet.addAll(addingConnections);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}

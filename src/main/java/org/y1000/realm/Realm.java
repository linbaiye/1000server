package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.network.Connection;
import org.y1000.network.NetworkEventListener;
import org.y1000.network.ConnectionEventType;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.CreatureManager;
import org.y1000.entities.players.PlayerManager;
import org.y1000.entities.players.Player;
import org.y1000.entities.PlayerVisibleScope;
import org.y1000.entities.repository.PlayerRepository;

import java.util.*;


@Slf4j
public final class Realm implements Runnable, NetworkEventListener {

    private static final long STEP_MILLIS = 50;

    private final PlayerManager playerManager;

    private final CreatureManager creatureManager;

    private final List<Connection> closingConnections;

    private final Map<Connection, Player> joiningPlayers;

    private final PlayerRepository playerRepository;

    private final RealmMap realmMap;

    private final List<Player> waitingPlayers;
    private final List<Player> leavingPlayers;


    private final

    public Realm(PlayerRepository playerRepository,
                 RealmMap map) {
        this.playerRepository = playerRepository;
        playerManager = new PlayerManager();
        realmMap = map;
        closingConnections = new ArrayList<>();
        joiningPlayers = new HashMap<>();
        creatureManager = new CreatureManager(playerManager, realmMap);
        waitingPlayers = new ArrayList<>(32);
        leavingPlayers = new ArrayList<>(32);
    }

    public RealmMap map() {
        return realmMap;
    }

    public synchronized void playerConnected(Connection connection, Player player) {
        joiningPlayers.put(connection, player);
        notify();
    }

    public synchronized void onConnectionClosed(Connection connection) {
        closingConnections.add(connection);
        notify();
    }


    private void sendVisibleCreatures(PlayerVisibleScope scope) {
        Set<Creature> creatures = creatureManager.visibleCreatures(scope);
        playerManager.sendVisibleCreatures(scope.getSource(), creatures);
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
            playerManager.add(c, p);
            p.registerEventListener(playerManager);
            p.joinReam(realmMap);
            playerManager.getVisibleScope(p)
                    .ifPresent(this::sendVisibleCreatures);
        });
    }


    @Override
    public void run() {
        try {
            long timeMillis = System.currentTimeMillis();
            while (true) {
                //handleConnectionEvents();
                long current = System.currentTimeMillis();
                if (timeMillis <= current) {
                    playerManager.update(STEP_MILLIS);
                    creatureManager.update(STEP_MILLIS);
                    timeMillis += STEP_MILLIS;
                } else {
                    synchronized (this) {
                        while (waitingPlayers.isEmpty() && leavingPlayers.isEmpty() &&
                                current < timeMillis) {
                            wait(timeMillis - current);
                            current = System.currentTimeMillis();
                        }
                        leavingPlayers.forEach(Player::leaveRealm);
                        waitingPlayers.forEach(player -> player.joinReam(realmMap));
                        notify();
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void OnEvent(ConnectionEventType type,
                        Connection connection) {
        if (type == ConnectionEventType.ESTABLISHED) {
            playerConnected(connection, playerRepository.load());
        } else if (type == ConnectionEventType.CLOSED) {
            onConnectionClosed(connection);
        }
    }

    public synchronized void addPlayer(Player player) {
        waitingPlayers.add(player);
        notifyAll();
    }

    public synchronized void removePlayer(Player player) {
        leavingPlayers.add(player);
        notifyAll();
    }

    public static Optional<Realm> create(String name
            , PlayerRepository playerRepository) {
        Optional<RealmMap> mapOptional = RealmMap.Load(name);
        return mapOptional.map(map -> new Realm(playerRepository, map));
    }
}

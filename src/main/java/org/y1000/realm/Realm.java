package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.PassiveMonster;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.*;


@Slf4j
public final class Realm implements Runnable {

    private static final long STEP_MILLIS = 50;

    private final RealmMap realmMap;

    private final List<Player> waitingPlayers;

    private final RealmEntityManager entityManager;

    private volatile boolean shutdown;

    public Realm(RealmMap map) {
        realmMap = map;
        waitingPlayers = new ArrayList<>(32);
        entityManager = new RealmEntityManager();
        shutdown = false;
    }

    public RealmMap map() {
        return realmMap;
    }

    private void joinPlayer(Player player) {
        entityManager.add(player);
        player.joinReam(realmMap);
    }

    private void initEntities() {
        List<PassiveMonster> monsters = List.of(new PassiveMonster(3L, new Coordinate(39, 30), Direction.DOWN, "牛", realmMap));
        monsters.forEach(entityManager::add);
    }


    private void step() {
        try {
            long timeMillis = System.currentTimeMillis();
            while (!shutdown) {
                long current = System.currentTimeMillis();
                if (timeMillis <= current) {
                    entityManager.updateEntities(STEP_MILLIS);
                    timeMillis += STEP_MILLIS;
                } else {
                    List<Player> tmp = Collections.emptyList();
                    synchronized (this) {
                        while (waitingPlayers.isEmpty() && current < timeMillis) {
                            wait(timeMillis - current);
                            current = System.currentTimeMillis();
                        }
                        if (!waitingPlayers.isEmpty()) {
                            tmp = new ArrayList<>(waitingPlayers);
                            waitingPlayers.clear();
                        }
                        notify();
                    }
                    tmp.forEach(this::joinPlayer);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        initEntities();
        step();
    }

    public synchronized void addPlayer(Player player) {
        waitingPlayers.add(player);
        notifyAll();
    }

    public void shutdown() {
        this.shutdown = true;
    }
}

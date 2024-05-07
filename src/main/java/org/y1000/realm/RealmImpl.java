package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.PassiveMonster;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.*;


@Slf4j
public final class RealmImpl implements Runnable, Realm {

    public static final int STEP_MILLIS = 10;

    private final RealmMap realmMap;

    private final List<Player> waitingPlayers;

    private final List<Player> leavingPlayers;

    private final RealmEntityManager entityManager;

    private volatile boolean shutdown;

    public RealmImpl(RealmMap map) {
        realmMap = map;
        waitingPlayers = new ArrayList<>(32);
        leavingPlayers = new ArrayList<>(32);
        entityManager = new RealmEntityManager();
        shutdown = false;
    }

    public RealmMap map() {
        return realmMap;
    }

    private void joinPlayer(Player player) {
        entityManager.add(player);
        player.joinReam(this);
    }


    private void initEntities() {
        List<PassiveMonster> monsters = List.of(new PassiveMonster(1000L, new Coordinate(39, 30), Direction.DOWN, "牛", realmMap));
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
                    List<Player> join = Collections.emptyList();
                    List<Player> leaving = Collections.emptyList();
                    synchronized (this) {
                        while (waitingPlayers.isEmpty() && current < timeMillis &&
                                leavingPlayers.isEmpty()) {
                            wait(timeMillis - current);
                            current = System.currentTimeMillis();
                        }
                        if (!waitingPlayers.isEmpty()) {
                            join = new ArrayList<>(waitingPlayers);
                            waitingPlayers.clear();
                        }
                        if (!leavingPlayers.isEmpty()) {
                            leaving = new ArrayList<>(leavingPlayers);
                            leavingPlayers.clear();
                        }
                        notifyAll();
                    }
                    join.forEach(this::joinPlayer);
                    leaving.forEach(Player::leaveRealm);
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

    public synchronized void removePlayer(Player player) {
        leavingPlayers.add(player);
        notifyAll();
    }

    @Override
    public Optional<Entity> findInsight(Entity source, long id) {
        return entityManager.findInsight(source, id);
    }
}

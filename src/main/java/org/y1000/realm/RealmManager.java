package org.y1000.realm;

import org.y1000.entities.players.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public final class RealmManager {
    private final Map<String, RealmImpl> realms;

    private final ExecutorService executorService;

    public RealmManager(Map<String, RealmImpl> realms) {
        this.realms = realms;
        executorService = Executors.newFixedThreadPool(realms.size());
    }

    public void start() {
        realms.values().forEach(executorService::submit);
    }

    public boolean shut() throws InterruptedException {
        executorService.shutdown();
        return executorService.awaitTermination(10, TimeUnit.SECONDS);
    }


    public RealmImpl load(String name) {
        return null;
    }

    public void onPlayerConnected(Player player, String realmName) {
        if (realms.containsKey(realmName)) {
            realms.get(realmName).addPlayer(player);
        }
    }

    public void onPlayerDisconnected(Player player) {
        player.leaveRealm();
    }

    public static RealmManager create() {
        Map<String, RealmImpl> realmMap = new HashMap<>();
        RealmImpl realm = RealmMap.Load("start")
                .map(RealmImpl::new)
                .orElseThrow(() -> new IllegalArgumentException("Map not found."));
        realmMap.put(realm.map().name(), realm);
        return new RealmManager(realmMap);
    }
}

package org.y1000.realm;

import org.y1000.entities.players.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public final class RealmManager {
    private final Map<String, RealmImpl> realms;

    private final Map<Player, RealmImpl> playerRealmMap;

    private final ExecutorService executorService;

    public RealmManager(Map<String, RealmImpl> realms) {
        this.realms = realms;
        executorService = Executors.newFixedThreadPool(realms.size());
        playerRealmMap = new HashMap<>();
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
            RealmImpl realm = realms.get(realmName);
            playerRealmMap.put(player, realm);
            realm.addPlayer(player);
        }
    }

    public void onPlayerDisconnected(Player player) {
        RealmImpl realm = playerRealmMap.get(player);
        if (realm != null) {
            realm.removePlayer(player);
            playerRealmMap.remove(player);
        }
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

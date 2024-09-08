package org.y1000.realm;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Player;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.util.Coordinate;

import java.util.Set;

interface PlayerManager extends ActiveEntityManager<Player> {
    void onPlayerConnected(Player player, Realm realm);

    void teleportIn(Player player,
                    Realm realm, Coordinate coordinate);

    void clearPlayer(Player player);

    void onClientEvent(PlayerDataEvent dataEvent,
                       ActiveEntityManager<Npc> npcManager);

    Set<Player> allPlayers();

    void onPlayerDisconnected(Player player);

}

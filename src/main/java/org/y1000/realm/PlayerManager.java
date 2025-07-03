package org.y1000.realm;

import org.y1000.entities.creatures.npc.INpc;
import org.y1000.entities.players.Player;
import org.y1000.message.input.Login;
import org.y1000.message.input.SelfHandleInput;
import org.y1000.network.Connection;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Optional;
import java.util.Set;

interface PlayerManager extends ActiveEntityManager<Player> {
    void onPlayerConnected(Player player, Realm realm);


    void loginPlayer(Player player, Login login, Realm realm);

    void logoutPlayer(Connection connection);

    void teleportIn(Player player,
                    Realm realm, Coordinate coordinate);

    void clearPlayer(Player player);

    void onClientEvent(PlayerDataEvent dataEvent,
                       ActiveEntityManager<INpc> npcManager);

    Set<Player> allPlayers();

    void onPlayerDisconnected(long playerId);

    void setTeleportHandler(UnaryAction<RealmTeleportEvent> teleportHandler);


    void handleInput(Connection connection, SelfHandleInput input);

    Optional<Player> find(Connection connection);

    default void shutdown() {

    }

}

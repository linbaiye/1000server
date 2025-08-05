package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.network.Connection;
import org.y1000.util.Coordinate;

public interface RealmEventHandler {

    void teleportIn(Player player, Coordinate toCoordinate, Connection connection);

    void broadcastText(BroadcastTextEvent event);

    void deliverPrivateChat(DeliveryPrivateChatEvent event);

    void deliverPrivateChatResult(long playerId, String reply);

    void handleProxiedLogin(long playerId, Coordinate toCoordinate, Connection connection);

}

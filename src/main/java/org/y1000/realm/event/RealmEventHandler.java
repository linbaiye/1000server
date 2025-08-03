package org.y1000.realm.event;

public interface RealmEventHandler {

    void handleTeleportEvent(RealmTeleportEvent teleportEvent);

    void broadcastText(BroadcastTextEvent event);

    void deliverPrivateChat(DeliveryPrivateChatEvent event);

    void deliverPrivateChatResult(long playerId, String reply);

}

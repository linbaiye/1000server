package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.MapSdb;

@Slf4j
final class ConjunctionDungeonRealm extends AbstractDungeonRealm {

    public ConjunctionDungeonRealm(int id,
                                   RealmMap realmMap,
                                   RealmEntityEventSender eventSender,
                                   GroundItemManager itemManager,
                                   NpcManager npcManager,
                                   PlayerManager playerManager,
                                   DynamicObjectManager dynamicObjectManager,
                                   TeleportManager teleportManager,
                                   CrossRealmEventSender crossRealmEventSender,
                                   MapSdb mapSdb,
                                   int interval,
                                   ChatManager chatManager) {
        super(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager, interval);
    }

    @Override
    Logger log() {
        return log;
    }

    @Override
    void handleTeleportEvent(RealmTeleportEvent teleportEvent) {
        acceptIfAffordableElseReject(teleportEvent);
    }
}

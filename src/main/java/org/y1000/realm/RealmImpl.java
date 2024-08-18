package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.MapSdb;

@Slf4j
final class RealmImpl extends AbstractRealm {

    public RealmImpl(int id, RealmMap realmMap,
                     RealmEntityEventSender eventSender,
                     GroundItemManager itemManager,
                     NpcManager npcManager,
                     PlayerManager playerManager,
                     DynamicObjectManager dynamicObjectManager,
                     TeleportManager teleportManager,
                     RealmEventHandler crossRealmEventHandler,
                     MapSdb mapSdb) {
        super(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb);
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    void handleTeleportEvent(RealmTeleportEvent teleportEvent) {
        acceptTeleport(teleportEvent);
    }

    @Override
    public void update() {
        doUpdateEntities();
    }

    @Override
    public String toString() {
        return "RealmImpl {id = " + id() + "}";
    }
}

package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.sdb.MapSdb;

@Slf4j
final class DungeonRealm extends AbstractRealm {

    public DungeonRealm(int id,
                        RealmMap realmMap,
                        RealmEntityEventSender eventSender,
                        ItemManagerImpl itemManager,
                        AbstractNpcManager npcManager,
                        PlayerManager playerManager,
                        DynamicObjectManager dynamicObjectManager,
                        TeleportManager teleportManager,
                        CrossRealmEventHandler crossRealmEventHandler,
                        MapSdb mapSdb, int interval) {
        super(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb);
    }

    @Override
    protected Logger log() {
        return log;
    }
}

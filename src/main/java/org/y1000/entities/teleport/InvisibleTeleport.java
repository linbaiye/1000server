package org.y1000.entities.teleport;

import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.UnaryAction;

import java.util.List;


public final class InvisibleTeleport extends AbstractTeleport {

    public InvisibleTeleport(long id, String idName,
                             CreateGateSdb createGateSdb,
                             UnaryAction<PlayerRealmEvent> teleportEventHandler,
                             int fromRealm,
                             List<TeleportCost> costList) {
        super(id, idName, createGateSdb, teleportEventHandler, fromRealm, costList);
    }
}

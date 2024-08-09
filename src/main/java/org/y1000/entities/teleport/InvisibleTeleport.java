package org.y1000.entities.teleport;

import org.y1000.realm.event.RealmEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.UnaryAction;

import java.util.function.BiConsumer;


public final class InvisibleTeleport extends AbstractTeleport {

    public InvisibleTeleport(long id, String idName,
                             CreateGateSdb createGateSdb,
                             UnaryAction<RealmEvent> teleportEventHandler) {
        super(id, idName, createGateSdb, teleportEventHandler);
    }
}

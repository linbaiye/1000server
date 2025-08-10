package org.y1000.entities.teleport;

import org.y1000.sdb.CreateGateSdb;

import java.util.function.Consumer;


public final class InvisibleTeleport extends AbstractTeleport {

    public InvisibleTeleport(long id, String idName,
                             CreateGateSdb createGateSdb,
                             TeleportEventHandler teleportHandler,
                             int fromRealm) {
        super(id, idName, createGateSdb, teleportHandler, fromRealm);
    }
}

package org.y1000.realm;


import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.teleport.*;
import org.y1000.sdb.CreateGateSdb;

import java.util.*;

@Slf4j
final class TeleportManager {

    private final CreateGateSdb createGateSdb;

    private final EntityIdGenerator entityIdGenerator;

    private final RealmMap realmMap;

    private final int realmId;

    private final AOIManager aoiManager;

    private final Set<StaticTeleport> staticTeleports;

    private final Set<Teleport> periodicOpenTeleports;

    public TeleportManager(int realmId,
                           RealmMap realmMap,
                           CreateGateSdb createGateSdb,
                           EntityIdGenerator entityIdGenerator,
                           AOIManager aoiManager) {
        this.createGateSdb = createGateSdb;
        this.entityIdGenerator = entityIdGenerator;
        this.realmMap = realmMap;
        this.realmId = realmId;
        this.aoiManager = aoiManager;
        staticTeleports = new HashSet<>();
        periodicOpenTeleports = new HashSet<>();
    }

    public void update() {
        periodicOpenTeleports.forEach(Teleport::tryAnnounce);
    }


    private void addTeleport(String idName, TeleportEventHandler teleportHandler) {
        Teleport teleport;
        if (createGateSdb.isVisible(idName)) {
            var port = new StaticTeleport(entityIdGenerator.next(), idName, createGateSdb, teleportHandler, realmId);
            staticTeleports.add(port);
            aoiManager.add(port);
            teleport = port;
        } else {
            teleport = new InvisibleTeleport(entityIdGenerator.next(), idName, createGateSdb, teleportHandler,  realmId);
        }
        realmMap.addTeleport(teleport);
        if (teleport.isPeriodic())
            periodicOpenTeleports.add(teleport);
        log.debug("Added port at {} in realm {}.", teleport.coordinate(), realmId);
    }

    public void init(TeleportEventHandler teleportHandler) {
        createGateSdb.getNames(realmId).forEach(name -> addTeleport(name, teleportHandler));
    }

    public Set<StaticTeleport> findStaticTeleports() {
        return staticTeleports;
    }
}

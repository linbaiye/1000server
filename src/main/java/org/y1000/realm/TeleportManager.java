package org.y1000.realm;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.teleport.*;
import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.UnaryAction;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
final class TeleportManager {

    private final CreateGateSdb createGateSdb;

    private final EntityIdGenerator entityIdGenerator;

    private final RealmMap realmMap;

    private final int realmId;

    private final AOIManager aoiManager;


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
    }


    private Teleport createTeleport(String idName, TeleportHandler teleportHandler) {
        Teleport teleport;
        if (createGateSdb.isVisible(idName)) {
            var port = new StaticTeleport(entityIdGenerator.next(), idName, createGateSdb, teleportHandler, realmId);
            aoiManager.add(port);
        }
    }


    private void addTeleport(String idName, TeleportHandler teleportHandler) {
        Teleport teleport;
        if (createGateSdb.isVisible(idName)) {
            var port = new StaticTeleport(entityIdGenerator.next(), idName, createGateSdb, teleportHandler, realmId);
            aoiManager.add(port);
            teleport = port;
        } else {
            teleport = new InvisibleTeleport(entityIdGenerator.next(), idName, createGateSdb, teleportHandler, realmId);
        }
        realmMap.addTeleport(teleport);
        log.debug("Added port at {} in realm {}.", teleport.coordinate(), realmId);
    }

    public void init(TeleportHandler teleportHandler) {
        createGateSdb.getNames(realmId).forEach(name -> addTeleport(name, teleportHandler));
    }

    public Set<StaticTeleport> findStaticTeleports() {
        return Collections.emptySet();
    }
}

package org.y1000.realm;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.teleport.InvisibleTeleport;
import org.y1000.entities.teleport.StaticTeleport;
import org.y1000.entities.teleport.Teleport;
import org.y1000.entities.teleport.TestingTeleport;
import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.UnaryAction;

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

    private void addTeleport(String idName, UnaryAction<PlayerRealmEvent> handler) {
        Teleport teleport;
        if (createGateSdb.isVisible(idName)) {
            var port = new StaticTeleport(entityIdGenerator.next(), idName, createGateSdb, handler);
            aoiManager.add(port);
            teleport = port;
        } else {
            teleport = new InvisibleTeleport(entityIdGenerator.next(), idName, createGateSdb, handler);
        }
        realmMap.addTeleport(teleport);
        log.debug("Added port at {} in realm {}.", teleport.coordinate(), realmId);
    }

    public void init(UnaryAction<PlayerRealmEvent> teleportEventHandler) {
        Validate.notNull(teleportEventHandler);
        if (realmId == 49) {
            realmMap.addTeleport(TestingTeleport.south(teleportEventHandler));
        }
        createGateSdb.getNames(realmId).forEach(name -> addTeleport(name, teleportEventHandler));
    }
}

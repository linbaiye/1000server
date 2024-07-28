package org.y1000.realm;


import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.teleport.InvisibleTeleport;
import org.y1000.entities.teleport.StaticTeleport;
import org.y1000.entities.teleport.Teleport;
import org.y1000.entities.teleport.TestingTeleport;
import org.y1000.realm.event.RealmEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public final class TeleportManager {

    private final CreateGateSdb createGateSdb;

    private final Set<Teleport> teleports;

    private final EntityIdGenerator entityIdGenerator;


    public TeleportManager(CreateGateSdb createGateSdb,
                           EntityIdGenerator entityIdGenerator) {
        this.createGateSdb = createGateSdb;
        this.entityIdGenerator = entityIdGenerator;
        this.teleports = new HashSet<>();
    }


    private void createTeleport(RealmMap realmMap, String gateName, UnaryAction<RealmEvent> teleportEventHandler) {
        if (createGateSdb.isVisible(gateName)) {
            return;
        }
        InvisibleTeleport teleport = new InvisibleTeleport(entityIdGenerator.next(), gateName, createGateSdb, teleportEventHandler);
        teleports.add(teleport);
        realmMap.addTeleport(teleport);
        log.debug("Added teleport at {}.", teleport.coordinate());
        /*Coordinate coordinate = Coordinate.xy(createGateSdb.getX(gateName), createGateSdb.getY(gateName));
        StaticTeleport teleport = StaticTeleport.builder().id(entityIdGenerator.next())
                .idName(gateName)
                .coordinate(coordinate)
                .createGateSdb(createGateSdb)
                .eventHandler(teleportEventHandler)
                .toCoordinate(Coordinate.xy(createGateSdb.getTX(gateName), createGateSdb.getTY(gateName)))
                .toRealm(createGateSdb.getServerId(gateName))
                .build();
        teleports.add(teleport);
        realmMap.addTeleport(teleport);*/
    }

    private void addTestTelerport(RealmMap realmMap, String gateName, UnaryAction<RealmEvent> teleportEventHandler) {
        InvisibleTeleport teleport = new InvisibleTeleport(entityIdGenerator.next(), gateName, createGateSdb, teleportEventHandler);
        teleports.add(teleport);
        realmMap.addTeleport(teleport);
    }

    private void addTeleports() {

    }

    public void init(RealmMap realmMap,
                     int realmId,
                     UnaryAction<RealmEvent> teleportEventHandler) {
        if (realmId == 49) {
            realmMap.addTeleport(new TestingTeleport(teleportEventHandler));
            return;
        }
        createGateSdb.getNames(realmId).forEach(name -> this.createTeleport(realmMap, name, teleportEventHandler));
    }
}

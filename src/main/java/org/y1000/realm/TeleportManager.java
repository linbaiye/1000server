package org.y1000.realm;


import org.y1000.entities.teleport.StaticTeleport;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.Set;

public final class TeleportManager {

    private final CreateGateSdb createGateSdb;

    private final Set<StaticTeleport> staticTeleports;

    private final EntityIdGenerator entityIdGenerator;

    public TeleportManager(CreateGateSdb createGateSdb,
                           EntityIdGenerator entityIdGenerator) {
        this.createGateSdb = createGateSdb;
        this.entityIdGenerator = entityIdGenerator;
        this.staticTeleports = new HashSet<>();
    }

    private void createTeleport(RealmMap realmMap, String gateName) {
        staticTeleports.add(new StaticTeleport(entityIdGenerator.next(), gateName, Coordinate.xy(createGateSdb.getTY())))
    }

    public void init(RealmMap realmMap,
                     int realmId) {
        createGateSdb.getNames(realmId).forEach(name -> this.createTeleport(realmMap, name));
    }
}

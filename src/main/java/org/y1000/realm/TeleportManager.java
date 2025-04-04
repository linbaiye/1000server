package org.y1000.realm;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.teleport.*;
import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.UnaryAction;

import java.util.*;

@Slf4j
final class TeleportManager {

    private final CreateGateSdb createGateSdb;

    private final EntityIdGenerator entityIdGenerator;

    private final RealmMap realmMap;

    private final int realmId;

    private final AOIManager aoiManager;

    private final Set<StaticTeleport> teleports;


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
        this.teleports = new HashSet<>();
    }

    private List<TeleportCost> loadCosts(String idName) {
        String needItem = createGateSdb.getNeedItem(idName);
        if (StringUtils.isEmpty(needItem)) {
            return Collections.emptyList();
        }
        String[] split = needItem.split(":");
        if (split.length != 2) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new ItemCost(split[0].trim(), Integer.parseInt(split[1].trim())));
    }

    private void addTeleport(String idName, UnaryAction<PlayerRealmEvent> handler) {
        Teleport teleport;
        List<TeleportCost> teleportCosts = loadCosts(idName);
        if (createGateSdb.isVisible(idName)) {
            var port = new StaticTeleport(entityIdGenerator.next(), idName, createGateSdb, handler, realmId, teleportCosts);
            aoiManager.add(port);
            teleports.add(port);
            teleport = port;
        } else {
            teleport = new InvisibleTeleport(entityIdGenerator.next(), idName, createGateSdb, handler, realmId, teleportCosts);
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

    public Set<StaticTeleport> findStaticTeleports() {
        return teleports;
    }
}

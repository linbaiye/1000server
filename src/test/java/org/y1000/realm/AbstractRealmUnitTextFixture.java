package org.y1000.realm;

import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public abstract class AbstractRealmUnitTextFixture extends AbstractUnitTestFixture {
    RealmMap realmMap;
    RealmEntityEventSender eventSender;
    GroundItemManager itemManager;
    NpcManager npcManager;
    PlayerManager playerManager;
    DynamicObjectManager dynamicObjectManager;
    TeleportManager teleportManager;
    RealmEventHandler crossRealmEventHandler;
    MapSdb mapSdb;

    CreateGateSdb createGateSdb;

    void setup() {
        eventSender = new RealmEntityEventSender(Mockito.mock(AOIManager.class));
        realmMap = mockRealmMap();
        itemManager = Mockito.mock(GroundItemManager.class);
        npcManager = Mockito.mock(NpcManager.class);
        playerManager = Mockito.mock(PlayerManager.class);
        dynamicObjectManager = Mockito.mock(DynamicObjectManager.class);
        createGateSdb = Mockito.mock(CreateGateSdb.class);
        when(createGateSdb.getNames(anyInt())).thenReturn(Collections.emptySet());
        teleportManager = new TeleportManager(1, realmMap, createGateSdb, new EntityIdGenerator(), new RelevantScopeManager());
        crossRealmEventHandler = Mockito.mock(RealmEventHandler.class);
        mapSdb = Mockito.mock(MapSdb.class);
    }

    DungeonRealm createDungeon(int interval, Supplier<LocalDateTime> dateTimeSupplier) {
        return new DungeonRealm(1, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb, interval, dateTimeSupplier);
    }

    DungeonRealm createHalfHourDungeon(Supplier<LocalDateTime> dateTimeSupplier) {
        return new DungeonRealm(1, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb, 180000, dateTimeSupplier);
    }

    DungeonRealm createOneHourDungeon(Supplier<LocalDateTime> dateTimeSupplier) {
        return new DungeonRealm(1, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb, 360000, dateTimeSupplier);
    }
}

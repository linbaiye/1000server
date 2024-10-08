package org.y1000.realm;

import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;

import javax.print.DocFlavor;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
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
    CrossRealmEventSender crossRealmEventSender;
    MapSdb mapSdb;

    CreateGateSdb createGateSdb;

    ChatManager chatManager;

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
        crossRealmEventSender = Mockito.mock(CrossRealmEventSender.class);
        mapSdb = Mockito.mock(MapSdb.class);
        chatManager = Mockito.mock(ChatManager.class);
    }

    EntranceDungeonRealm createDungeon(int interval, Supplier<LocalDateTime> dateTimeSupplier, Set<Integer> wl) {
        return new EntranceDungeonRealm(1, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb,
                interval, dateTimeSupplier, chatManager, wl);
    }

    EntranceDungeonRealm createDungeon(int interval, Supplier<LocalDateTime> dateTimeSupplier) {
        return createDungeon(interval, dateTimeSupplier, Collections.emptySet());
    }

    EntranceDungeonRealm createHalfHourDungeon(Supplier<LocalDateTime> dateTimeSupplier) {
        return createDungeon(180000, dateTimeSupplier);
    }

    EntranceDungeonRealm createOneHourDungeon(Supplier<LocalDateTime> dateTimeSupplier) {
        return createDungeon(360000, dateTimeSupplier);
    }

    EntranceDungeonRealm createWhitelisted(Supplier<LocalDateTime> dateTimeSupplier, Set<Integer> ids) {
        return createDungeon(360000, dateTimeSupplier, ids);
    }
}

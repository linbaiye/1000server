package org.y1000.realm;

import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public abstract class AbstractRealmUnitTextFixture extends AbstractUnitTestFixture {
    RealmMap realmMap;
    MessageSender eventSender;
    GroundItemManager itemManager;
    NpcManager npcManager;
    PlayerManager playerManager;
    DynamicObjectManager dynamicObjectManager;
    TeleportManager teleportManager;
    RealmEventSender crossRealmEventSender;
    MapSdb mapSdb;

    CreateGateSdb createGateSdb;


    PlayerRepository playerRepository;

    void setup() {
        //eventSender = new RealmPlayerConnectionManager(Mockito.mock(AOIManager.class));
        eventSender = Mockito.mock(MessageSender.class);
        realmMap = mockRealmMap();
        itemManager = Mockito.mock(GroundItemManager.class);
        npcManager = Mockito.mock(NpcManager.class);
        playerManager = Mockito.mock(PlayerManager.class);
        dynamicObjectManager = Mockito.mock(DynamicObjectManager.class);
        createGateSdb = Mockito.mock(CreateGateSdb.class);
        when(createGateSdb.getNames(anyInt())).thenReturn(Collections.emptySet());
        teleportManager = new TeleportManager(1, realmMap, createGateSdb, new EntityIdGenerator(), new RelevantScopeManager());
        crossRealmEventSender = Mockito.mock(RealmEventSender.class);
        mapSdb = Mockito.mock(MapSdb.class);
        playerRepository = Mockito.mock(PlayerRepository.class);
    }

    DungeonRealm createDungeon(int interval, Supplier<LocalDateTime> dateTimeSupplier, Set<Integer> wl) {
        return new DungeonRealm(1, realmMap, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, playerRepository, interval);
    }

    DungeonRealm createDungeon(int interval, Supplier<LocalDateTime> dateTimeSupplier) {
        return createDungeon(interval, dateTimeSupplier, Collections.emptySet());
    }

    DungeonRealm createHalfHourDungeon(Supplier<LocalDateTime> dateTimeSupplier) {
        return createDungeon(180000, dateTimeSupplier);
    }

    DungeonRealm createOneHourDungeon(Supplier<LocalDateTime> dateTimeSupplier) {
        return createDungeon(360000, dateTimeSupplier);
    }

    DungeonRealm createWhitelisted(Supplier<LocalDateTime> dateTimeSupplier, Set<Integer> ids) {
        return createDungeon(360000, dateTimeSupplier, ids);
    }
}

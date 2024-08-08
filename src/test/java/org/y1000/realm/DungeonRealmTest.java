package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


class DungeonRealmTest extends AbstractUnitTestFixture {
    private DungeonRealm dungeonRealm;
    RealmMap realmMap;
    RealmEntityEventSender eventSender;
    GroundItemManager itemManager;
    AbstractNpcManager npcManager;
    PlayerManager playerManager;
    DynamicObjectManager dynamicObjectManager;
    TeleportManager teleportManager;
    CrossRealmEventHandler crossRealmEventHandler;
    MapSdb mapSdb;

    CreateGateSdb createGateSdb;

    private

    @BeforeEach
    void setUp() {
        eventSender = new RealmEntityEventSender(Mockito.mock(AOIManager.class));
        realmMap = mockRealmMap();
        itemManager = Mockito.mock(GroundItemManager.class);
        npcManager = Mockito.mock(AbstractNpcManager.class);
        playerManager = Mockito.mock(PlayerManager.class);
        dynamicObjectManager = Mockito.mock(DynamicObjectManager.class);
        createGateSdb = Mockito.mock(CreateGateSdb.class);
        when(createGateSdb.getNames(anyInt())).thenReturn(Collections.emptySet());
        teleportManager = new TeleportManager(createGateSdb, new EntityIdGenerator());
        crossRealmEventHandler = Mockito.mock(CrossRealmEventHandler.class);
        mapSdb = Mockito.mock(MapSdb.class);
    }

    private void buildRealm() {
        dungeonRealm = new DungeonRealm(1L, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, 180000, )
    }

    @Test
    void name() {
    }
}
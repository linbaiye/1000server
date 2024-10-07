package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.Player;
import org.y1000.entities.teleport.StaticTeleport;
import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.UnaryAction;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TeleportManagerTest extends AbstractUnitTestFixture {

    private CreateGateSdb createGateSdb;

    private EntityIdGenerator entityIdGenerator;

    private RealmMap realmMap;

    private final int realmId = 1;

    private AOIManager aoiManager;

    private TeleportManager manager;

    private UnaryAction<PlayerRealmEvent> eventUnaryAction;

    private PlayerRealmEvent event;

    @BeforeEach
    void setUp() {
        entityIdGenerator = new EntityIdGenerator();
        realmMap = Mockito.mock(RealmMap.class);
        createGateSdb = Mockito.mock(CreateGateSdb.class);
        aoiManager = Mockito.mock(AOIManager.class);
        manager = new TeleportManager(realmId, realmMap, createGateSdb, entityIdGenerator, aoiManager);
        eventUnaryAction = e -> event = e;
    }

    @Test
    void init() {
        when(createGateSdb.isVisible(anyString())).thenReturn(true);
        when(createGateSdb.getNames(realmId)).thenReturn(Set.of("test"));
        when(createGateSdb.getX("test")).thenReturn(1);
        when(createGateSdb.getY("test")).thenReturn(1);
        when(createGateSdb.getViewName("test")).thenReturn("view");
        manager.init(eventUnaryAction);
        assertFalse(manager.findStaticTeleports().isEmpty());
    }

    @Test
    void withCosts() {
        when(createGateSdb.isVisible(anyString())).thenReturn(true);
        when(createGateSdb.getNames(realmId)).thenReturn(Set.of("test"));
        when(createGateSdb.getX("test")).thenReturn(1);
        when(createGateSdb.getY("test")).thenReturn(1);
        when(createGateSdb.getViewName("test")).thenReturn("view");
        when(createGateSdb.getNeedItem("test")).thenReturn("gold:1");
        manager.init(eventUnaryAction);
        StaticTeleport staticTeleport = manager.findStaticTeleports().stream().findFirst().get();
        var player = Mockito.mock(Player.class);
        staticTeleport.teleport(player);
        RealmTeleportEvent teleportEvent = (RealmTeleportEvent) event;
        assertFalse(teleportEvent.getCosts().isEmpty());
    }
}
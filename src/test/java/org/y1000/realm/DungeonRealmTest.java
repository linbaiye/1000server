package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.ServerMessage;
import org.y1000.network.Connection;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


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

    private LocalDateTime currentDateTime;

    private int interval = 180000;

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
        dungeonRealm = new DungeonRealm(1, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb, interval, () -> currentDateTime);
    }

    @Test
    void enterWhenHalfHourNotOpen() {
        buildRealm();
        PlayerImpl player = playerBuilder().build();
        Connection connection = Mockito.mock(Connection.class);
        AtomicReference<ServerMessage> messageAtomicReference = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            ServerMessage argument = invocationOnMock.getArgument(0);
            messageAtomicReference.set(argument);
            return null;
        }).when(connection).write(any(ServerMessage.class));
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection);

        currentDateTime  = LocalDateTime.now().withMinute(6).withSecond(0);
        dungeonRealm.handle(realmTeleportEvent);
        verify(connection, times(1)).write(any(PlayerTextEvent.class));
        assertTrue(messageAtomicReference.get().toPacket().getText().getText().contains("24分后"));

        currentDateTime = LocalDateTime.now().withMinute(29).withSecond(29);
        dungeonRealm.handle(realmTeleportEvent);
        assertTrue(messageAtomicReference.get().toPacket().getText().getText().contains("1秒后"));

        currentDateTime = LocalDateTime.now().withMinute(5).withSecond(1);
        dungeonRealm.handle(realmTeleportEvent);
        assertTrue(messageAtomicReference.get().toPacket().getText().getText().contains("24分59秒后"));

        currentDateTime = LocalDateTime.now().withMinute(50).withSecond(1);
        dungeonRealm.handle(realmTeleportEvent);
        assertTrue(messageAtomicReference.get().toPacket().getText().getText().contains("9分59秒后"));

        currentDateTime = LocalDateTime.now().withMinute(5).withSecond(0);
        dungeonRealm.handle(realmTeleportEvent);
        assertTrue(messageAtomicReference.get().toPacket().getText().getText().contains("25分后"));
    }

    @Test
    void enterHalfHourWhenOpen() {
        buildRealm();
        Player player = playerBuilder().build();
        Connection connection = Mockito.mock(Connection.class);
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection);
        currentDateTime  = LocalDateTime.now().withMinute(0).withSecond(0);
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(1)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));
        currentDateTime  = LocalDateTime.now().withMinute(4).withSecond(59);
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(2)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));
    }

    @Test
    void enterOneHourWhenNotOpen() {
        interval = 360000;
        buildRealm();
        PlayerImpl player = playerBuilder().build();
        Connection connection = Mockito.mock(Connection.class);
        AtomicReference<ServerMessage> messageAtomicReference = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            ServerMessage argument = invocationOnMock.getArgument(0);
            messageAtomicReference.set(argument);
            return null;
        }).when(connection).write(any(ServerMessage.class));
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection);
        currentDateTime  = LocalDateTime.now().withMinute(5).withSecond(0);
        dungeonRealm.handle(realmTeleportEvent);
        verify(connection, times(1)).write(any(PlayerTextEvent.class));
        assertTrue(messageAtomicReference.get().toPacket().getText().getText().contains("55分后"));
    }

    @Test
    void enterOneHourWhenOpen() {
        interval = 360000;
        buildRealm();
        Player player = playerBuilder().build();
        Connection connection = Mockito.mock(Connection.class);
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection);
        currentDateTime  = LocalDateTime.now().withMinute(0).withSecond(0);
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(1)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));
        currentDateTime  = LocalDateTime.now().withMinute(4).withSecond(59);
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(2)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));
    }
}
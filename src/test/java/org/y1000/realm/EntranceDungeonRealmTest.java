package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.ServerMessage;
import org.y1000.network.Connection;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.PlayerRealmEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class EntranceDungeonRealmTest extends AbstractRealmUnitTextFixture {
    private EntranceDungeonRealm dungeonRealm;

    private LocalDateTime currentDateTime;

    private int interval = 180000;

    @BeforeEach
    void setUp() {
        setup();
    }

    private void buildRealm() {
        dungeonRealm = createDungeon(interval, () -> currentDateTime);
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
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection, 0);

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
        player.leaveRealm();
        Connection connection = Mockito.mock(Connection.class);
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection, 0);
        currentDateTime  = LocalDateTime.now().withMinute(0).withSecond(0);
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(1)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));
        currentDateTime  = LocalDateTime.now().withMinute(4).withSecond(59);
        player.leaveRealm();
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
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection, 0);
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
        player.leaveRealm();
        Connection connection = Mockito.mock(Connection.class);
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection, 0);
        currentDateTime  = LocalDateTime.now().withMinute(0).withSecond(0);
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(1)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));
        currentDateTime  = LocalDateTime.now().withMinute(4).withSecond(59);
        player.leaveRealm();
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(2)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));
    }


    @Test
    void close() {
        buildRealm();
        Player player = playerBuilder().build();
        when(playerManager.allPlayers()).thenReturn(Collections.singleton(player));
        when(playerManager.contains(player)).thenReturn(true);
        dungeonRealm.close();
        verify(crossRealmEventSender, times(1)).send(any(RealmTeleportEvent.class));
        dungeonRealm.close();
        verify(crossRealmEventSender, times(1)).send(any(RealmTeleportEvent.class));
    }

    @Test
    void enterFromWhitelistedRealm() {
        interval = 360000;
        PlayerImpl player = playerBuilder().build();
        Connection connection = Mockito.mock(Connection.class);
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection, 1);
        currentDateTime  = LocalDateTime.now().withMinute(10).withSecond(0);
        dungeonRealm = createWhitelisted(() -> currentDateTime, Set.of(1));
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(1)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));
    }

    @Test
    void connectionWillBeTeleported() {
        buildRealm();
        Player player = playerBuilder().build();
        when(playerManager.contains(player)).thenReturn(true);
        Connection connection = Mockito.mock(Connection.class);
        dungeonRealm.handle(new ConnectionEstablishedEvent(dungeonRealm.id(), player, connection));
        verify(playerManager, times(1)).onPlayerConnected(any(Player.class), any(Realm.class));
        verify(crossRealmEventSender, times(1)).send(any(PlayerRealmEvent.class));
    }
}
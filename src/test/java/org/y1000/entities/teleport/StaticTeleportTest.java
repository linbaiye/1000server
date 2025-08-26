package org.y1000.entities.teleport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.sdb.CreateGateSdb;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class StaticTeleportTest {



    private CreateGateSdb createGateSdb;

    private TeleportEventHandler teleportEventHandler;

    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        createGateSdb = Mockito.mock(CreateGateSdb.class);
        teleportEventHandler = Mockito.mock(TeleportEventHandler.class);
        when(createGateSdb.getTX(anyString())).thenReturn(3);
        when(createGateSdb.getTY(anyString())).thenReturn(4);
        when(createGateSdb.getX(anyString())).thenReturn(1);
        when(createGateSdb.getY(anyString())).thenReturn(2);
        when(createGateSdb.getServerId(anyString())).thenReturn(2);
        when(createGateSdb.getMapId(anyString())).thenReturn(1);
        when(createGateSdb.getWidth(anyString())).thenReturn(1);
        when(createGateSdb.getEX(anyString())).thenReturn(5);
        when(createGateSdb.getEY(anyString())).thenReturn(6);
        when(createGateSdb.getShape(anyString())).thenReturn(10);
        when(createGateSdb.getRandomPos(anyString())).thenReturn("");
        when(createGateSdb.getViewName(anyString())).thenReturn("test");
        when(createGateSdb.getAnnouncement(anyString())).thenReturn("open in 5 minutes");
    }

    @Test
    void announceHourly() {
        when(createGateSdb.getRegenInterval(anyString())).thenReturn(360000);
        time = LocalDateTime.now().withMinute(55).withSecond(0);
        var teleport = new StaticTeleport(1, "test", createGateSdb, teleportEventHandler, 1, () -> time);
        teleport.tryAnnounce();
        verify(teleportEventHandler, times(0)).announceDungeonOpen(anyString());
        time = time.plusHours(1).withMinute(54).withSecond(59);
        teleport.tryAnnounce();
        verify(teleportEventHandler, times(0)).announceDungeonOpen(anyString());
        time = time.withMinute(55).withSecond(0);
        teleport.tryAnnounce();
        verify(teleportEventHandler, times(1)).announceDungeonOpen(anyString());
        teleport.tryAnnounce();
        verify(teleportEventHandler, times(1)).announceDungeonOpen(anyString());
    }
}
package org.y1000.entities.teleport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class InvisibleTeleportTest {

    private CreateGateSdb createGateSdb;

    @BeforeEach
    void setUp() {
        createGateSdb = Mockito.mock(CreateGateSdb.class);
        when(createGateSdb.getRandomPos(anyString())).thenReturn("12:11:33:44");
        when(createGateSdb.getTX(anyString())).thenReturn(1);
        when(createGateSdb.getTY(anyString())).thenReturn(1);
        when(createGateSdb.getServerId(anyString())).thenReturn(1);
        when(createGateSdb.getWidth(anyString())).thenReturn(2);
    }

    @Test
    void randomPosition() {
        var teleport = new InvisibleTeleport(1L, "te", createGateSdb, e -> {});
        assertEquals(9, teleport.teleportCoordinates().size());
        assertTrue(teleport.coordinate().equals(Coordinate.xy(12, 11)) || teleport.coordinate().equals(Coordinate.xy(33, 44)));
        assertEquals(1L, teleport.id());
    }
}
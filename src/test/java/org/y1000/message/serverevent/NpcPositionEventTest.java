package org.y1000.message.serverevent;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.NpcPositionPacket;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class NpcPositionEventTest {


    @Test
    void packet() {
        var m1 = Mockito.mock(Merchant.class);
        when(m1.coordinate()).thenReturn(Coordinate.xy(1, 1));
        when(m1.viewName()).thenReturn("m1");
        var player = Mockito.mock(Player.class);
        NpcPositionEvent event = new NpcPositionEvent(player, List.of(m1), Collections.emptyList());
        NpcPositionPacket npcPosition = event.toPacket().getNpcPosition();
        assertEquals(1, npcPosition.getXListList().get(0));
        assertEquals(1, npcPosition.getYListList().get(0));
        assertEquals("m1", npcPosition.getNameListList().get(0));
    }
}
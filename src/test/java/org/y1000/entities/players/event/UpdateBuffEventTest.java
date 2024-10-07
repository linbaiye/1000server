package org.y1000.entities.players.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.item.BuffPill;
import org.y1000.network.gen.UpdateBuffPacket;

import static org.junit.jupiter.api.Assertions.*;

class UpdateBuffEventTest extends AbstractUnitTestFixture {


    private Player player;

    @BeforeEach
    void setUp() {
        player = Mockito.mock(Player.class);
    }

    @Test
    void fade() {
        UpdateBuffEvent event = UpdateBuffEvent.fade(player);
        assertEquals(2, event.toPacket().getUpdateBuff().getType());
        assertTrue(event.visibleToSelf());
    }

    @Test
    void gain() {
        BuffPill buffPill = new BuffPill("test", "test", "test", "desc", 10, 1000, 2);
        UpdateBuffEvent event = UpdateBuffEvent.gain(player, buffPill);
        assertTrue(event.visibleToSelf());
        UpdateBuffPacket updateBuff = event.toPacket().getUpdateBuff();
        assertEquals(1, updateBuff.getType());
        assertEquals("desc", updateBuff.getDescription());
        assertEquals(1, updateBuff.getSeconds());
        assertEquals(2, updateBuff.getIcon());
    }
}
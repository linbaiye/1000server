package org.y1000.message.clientevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.AbstractItemUnitTestFixture;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;


import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

class ClientDyeEventTest extends AbstractItemUnitTestFixture {

    private Inventory inventory;
    private Player player;

    @BeforeEach
    void setUp() {
        player = Mockito.mock(Player.class);
        inventory = new Inventory();
        when(player.inventory()).thenReturn(inventory);
    }

    @Test
    void handle() {
        int dyedSlot = inventory.add(itemFactory.createItem("女子皮鞋"));
        var before = inventory.getItem(dyedSlot).color();
        int dyeSlot = inventory.add(itemFactory.createItem("蓝色染剂"));
        new ClientDyeEvent(dyedSlot, dyeSlot).handle(player);
        assertNotEquals(before, inventory.getItem(dyedSlot).color());
        verify(player, times(1)).consumeItem(dyeSlot);
        verify(player, times(1)).emitEvent(any(UpdateInventorySlotEvent.class));
    }
}
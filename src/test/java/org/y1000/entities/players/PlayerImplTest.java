package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.entities.players.kungfu.KungFuBook;
import org.y1000.entities.players.kungfu.attack.QuanfaKungFu;
import org.y1000.item.Hat;
import org.y1000.message.clientevent.ClientDoubleClickSlotEvent;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventListener;
import org.y1000.message.serverevent.PlayerEquipEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.util.Coordinate;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class PlayerImplTest implements EntityEventListener {

    private Player player;

    private Queue<EntityEvent> events;

    @BeforeEach
    public void setup() {
        events = new ArrayDeque<>();
    }

    private PlayerImpl.PlayerImplBuilder builder() {
        return PlayerImpl.builder()
                .id(1L)
                .coordinate(new Coordinate(1, 1))
                .name("test")
                .kungFuBook(KungFuBook.newInstance())
                .attackKungFu(QuanfaKungFu.unnamed())
                .inventory(new Inventory());
    }


    @Test
    void hat() {
        player = builder().hat(new Hat("test")).build();
        assertEquals("test", player.hat().get().name());
    }

    @Test
    void equipHatEvent() {
        Inventory inventory = new Inventory();
        int slot = inventory.add(new Hat("test"));
        player = builder().inventory(inventory).build();
        player.registerOrderedEventListener(this);
        player.handleEvent(new ClientDoubleClickSlotEvent(slot));
        PlayerEquipEvent first = (PlayerEquipEvent)events.poll();
        assertEquals(first.player(), player);
        assertEquals(first.toPacket().getEquip().getEquipmentName(), "test");
        UpdateInventorySlotEvent second = (UpdateInventorySlotEvent)events.poll();
        assertEquals(second.toPacket().getUpdateSlot().getSlotId(), slot);
        assertEquals(second.toPacket().getUpdateSlot().getName(), "");
    }

    @Override
    public void OnEvent(EntityEvent entityEvent) {
        events.add(entityEvent);
    }
}
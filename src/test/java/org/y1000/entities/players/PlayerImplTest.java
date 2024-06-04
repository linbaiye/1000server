package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingPlayerEventListener;
import org.y1000.entities.players.event.PlayerToggleKungFuEvent;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.Weapon;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.item.Hat;
import org.y1000.message.clientevent.ClientDoubleClickSlotEvent;
import org.y1000.message.serverevent.PlayerEquipEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;

import static org.junit.jupiter.api.Assertions.*;

class PlayerImplTest extends AbstractUnitTestFixture {

    private PlayerImpl player;

    private TestingPlayerEventListener eventListener;

    private Inventory inventory;

    @BeforeEach
    public void setup() {
        eventListener = new TestingPlayerEventListener();
        inventory = new Inventory();
        player = playerBuilder().inventory(inventory).build();
        player.registerOrderedEventListener(eventListener);
    }


    @Test
    void hat() {
        player = playerBuilder().hat(new Hat("test")).build();
        assertEquals("test", player.hat().get().name());
    }

    @Test
    void equipHatEvent() {
        Inventory inventory = new Inventory();
        int slot = inventory.add(new Hat("test"));
        player = playerBuilder().inventory(inventory).build();
        player.registerOrderedEventListener(eventListener);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        PlayerEquipEvent first = eventListener.dequeue(PlayerEquipEvent.class);
        assertEquals(first.player(), player);
        assertEquals(first.toPacket().getEquip().getEquipmentName(), "test");
        UpdateInventorySlotEvent second = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals(second.toPacket().getUpdateSlot().getSlotId(), slot);
        assertEquals(second.toPacket().getUpdateSlot().getName(), "");
    }

    @Test
    void equipNotSameTypeWeapon() {
        Weapon test = new Weapon("sword", AttackKungFuType.SWORD);
        int slot = inventory.add(test);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot));
        assertTrue(player.weapon().isPresent());
        assertSame(player.attackKungFu().getType(), AttackKungFuType.SWORD);
        assertTrue(player.cooldown() != 0);
        var removeInventorySlotEvent = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals(removeInventorySlotEvent.toPacket().getUpdateSlot().getSlotId(), slot);
        PlayerEquipEvent equipEvent = eventListener.dequeue(PlayerEquipEvent.class);
        assertEquals(equipEvent.toPacket().getEquip().getEquipmentName(), "sword");
        PlayerToggleKungFuEvent kungFuEvent = eventListener.dequeue(PlayerToggleKungFuEvent.class);
        assertEquals(kungFuEvent.toPacket().getToggleKungFu().getName(), player.kungFuBook().findUnnamed(AttackKungFuType.SWORD).name());
        assertNull(inventory.getItem(slot));
    }

    @Test
    void equipWeapon_switchEquipped() {
        Weapon sword = new Weapon("sword", AttackKungFuType.SWORD);
        int slot1 = inventory.add(sword);
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot1));
        eventListener.clearEvents();
        var axe = new Weapon("axe", AttackKungFuType.AXE);
        int slot2 = inventory.add(axe);

        // act
        player.handleClientEvent(new ClientDoubleClickSlotEvent(slot2));
        assertEquals(player.attackKungFu().name(), player.kungFuBook().findUnnamed(AttackKungFuType.AXE).name());
        assertSame(inventory.getItem(slot2), sword);
        assertTrue(player.weapon().isPresent());
        player.weapon().ifPresent(weapon -> assertSame(axe, weapon));
        var removeItemEvent = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals(removeItemEvent.toPacket().getUpdateSlot().getSlotId(), slot2);
        var putItemEvent = eventListener.dequeue(UpdateInventorySlotEvent.class);
        //assertEquals(pu);
    }
}
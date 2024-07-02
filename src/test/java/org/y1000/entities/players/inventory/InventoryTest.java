package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.players.Player;
import org.y1000.item.Hat;
import org.y1000.item.StackItem;
import org.y1000.item.Weapon;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.event.EntityEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.trade.TradeItem;
import org.y1000.util.UnaryAction;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class InventoryTest {

    private Inventory inventory;

    private Player player;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        player = Mockito.mock(Player.class);
    }

    @Test
    void findByType() {
        inventory.add(new Weapon("test", AttackKungFuType.SWORD));
        Optional<Weapon> weapon = inventory.findWeapon(AttackKungFuType.SWORD);
        assertTrue(weapon.isPresent());
        weapon.ifPresent(w -> assertEquals(w.kungFuType(), AttackKungFuType.SWORD));
    }

    @Test
    void findSlot() {
        inventory.add(new Weapon("test", AttackKungFuType.SWORD));
        int weaponSlot = inventory.findWeaponSlot(AttackKungFuType.SWORD);
        assertEquals(1, weaponSlot);
        assertEquals(0, inventory.findWeaponSlot(AttackKungFuType.AXE));
    }

    @Test
    void add() {
        assertEquals(0, inventory.add(null));
        assertEquals(1, inventory.add(new Hat("hat")));
        assertEquals(2, inventory.add(new Hat("hat2")));
        assertEquals(3, inventory.add(StackItem.money(100)));
        assertEquals(3, inventory.add(StackItem.money(100)));
        for (int i = 0; i < inventory.maxCapacity() - 3; i++) {
            assertNotEquals(0, inventory.add(new Hat("hat")));
        }
        assertEquals(0, inventory.add(new Hat("hat")));
    }

    @Test
    void consumeStackItem() {
        TestingEventListener eventListener = new TestingEventListener();
        assertFalse(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        inventory.add(new StackItem("箭", 2));
        assertTrue(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        UpdateInventorySlotEvent event = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals("箭", event.toPacket().getUpdateSlot().getName());
        assertEquals(1, event.toPacket().getUpdateSlot().getNumber());
        assertTrue(inventory.contains("箭"));

        assertTrue(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        event = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals("", event.toPacket().getUpdateSlot().getName());
        assertFalse(event.toPacket().getUpdateSlot().hasNumber());
        assertFalse(inventory.contains("箭"));
    }

    @Test
    void canSell() {
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1));
        assertFalse(inventory.canSell(items));
        inventory.add(new StackItem("肉", 2));
        assertTrue(inventory.canSell(items));
        for (int i = 0; i < inventory.maxCapacity() - 1; i++) {
            inventory.add(new Hat("hat"));
        }
        assertFalse(inventory.canSell(items));
        inventory.remove(inventory.maxCapacity());
        assertTrue(inventory.canSell(items));
        inventory.add(StackItem.money(1));
        assertTrue(inventory.isFull());
        assertTrue(inventory.canSell(items));
    }

    @Test
    void sell() {
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1));
        inventory.add(new StackItem("肉", 2));
        var player = Mockito.mock(Player.class);
        inventory.sell(items, 4, player);
        assertTrue(inventory.findFirstStackItem("肉").isPresent());
        inventory.findFirstStackItem("肉").ifPresent( stackItem -> assertEquals(1, stackItem.number()));
        inventory.findFirstStackItem(StackItem.MONEY).ifPresent( stackItem -> assertEquals(4, stackItem.number()));
    }
}
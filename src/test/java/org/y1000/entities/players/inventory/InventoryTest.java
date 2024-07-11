package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.players.Player;
import org.y1000.item.*;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.trade.TradeItem;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class InventoryTest {

    private Inventory inventory;

    private Player player;

    private ItemSdb itemSdb;

    @BeforeEach
    void setUp() {
        itemSdb = Mockito.mock(ItemSdb.class);
        when(itemSdb.getAttackKungFuType(anyString())).thenReturn(AttackKungFuType.SWORD);
        inventory = new Inventory();
        player = Mockito.mock(Player.class);
    }

    private Hair createHair() {
        return new Hair("男子长发", ItemSdbImpl.INSTANCE);
    }

    @Test
    void findByType() {
        inventory.add(new Weapon("test", itemSdb));
        Optional<Weapon> weapon = inventory.findWeapon(AttackKungFuType.SWORD);
        assertTrue(weapon.isPresent());
        weapon.ifPresent(w -> assertEquals(w.kungFuType(), AttackKungFuType.SWORD));
    }

    @Test
    void findSlot() {
        inventory.add(new Weapon("test", itemSdb));
        int weaponSlot = inventory.findWeaponSlot(AttackKungFuType.SWORD);
        assertEquals(1, weaponSlot);
        assertEquals(0, inventory.findWeaponSlot(AttackKungFuType.AXE));
    }

    @Test
    void add() {
        assertEquals(0, inventory.add(null));
        assertEquals(1, inventory.add(createHair()));
        assertEquals(2, inventory.add(createHair()));
        assertEquals(3, inventory.add(DefaultStackItem.money(100)));
        assertEquals(3, inventory.add(DefaultStackItem.money(100)));
        for (int i = 0; i < inventory.maxCapacity() - 3; i++) {
            assertNotEquals(0, inventory.add(createHair()));
        }
        assertEquals(0, inventory.add(createHair()));
    }

    @Test
    void consumeStackItem() {
        TestingEventListener eventListener = new TestingEventListener();
        assertFalse(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        inventory.add(new DefaultStackItem("箭", 2));
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
        inventory.add(new DefaultStackItem("肉", 2));
        assertTrue(inventory.canSell(items));
        for (int i = 0; i < inventory.maxCapacity() - 1; i++) {
            inventory.add(createHair());
        }
        assertFalse(inventory.canSell(items));
        inventory.remove(inventory.maxCapacity());
        assertTrue(inventory.canSell(items));
        inventory.add(DefaultStackItem.money(1));
        assertTrue(inventory.isFull());
        assertTrue(inventory.canSell(items));
    }

    @Test
    void sell() {
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1));
        inventory.add(new DefaultStackItem("肉", 2));
        var player = Mockito.mock(Player.class);
        inventory.sell(items, 4, player);
        assertTrue(inventory.findFirstStackItem("肉").isPresent());
        inventory.findFirstStackItem("肉").ifPresent( stackItem -> assertEquals(1, stackItem.number()));
        inventory.findFirstStackItem(DefaultStackItem.MONEY).ifPresent(stackItem -> assertEquals(4, stackItem.number()));
    }

    @Test
    void canBuy() {
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 30));
        assertFalse(inventory.canBuy(items, 10));
        inventory.add(DefaultStackItem.money(100));
        assertTrue(inventory.canBuy(items, 10));
        assertFalse(inventory.canBuy(items, 1000));
        int slot = inventory.emptySlotSize() - 1;
        for (int i = 0; i < slot; i++) {
            inventory.add(createHair());
        }
        assertTrue(inventory.canBuy(items, 10));

        // make inventory full.
        inventory.add(createHair());
        assertFalse(inventory.canBuy(items, 10));
    }

    @Test
    void buy() {
        List<TradeItem> items = List.of(new TradeItem("药1", 1, inventory.maxCapacity()));
        inventory.add(DefaultStackItem.money(30));
        var player = Mockito.mock(Player.class);
        inventory.buy(items, 30, player, DefaultStackItem::new);
        assertTrue(inventory.findFirstStackItem(DefaultStackItem.MONEY).isEmpty());
        assertEquals(inventory.maxCapacity(), inventory.findFirstSlot("药1"));

        inventory = new Inventory();
        inventory.add(DefaultStackItem.money(50));
        inventory.buy(items, 30, player, DefaultStackItem::new);
        assertTrue(inventory.findFirstStackItem(DefaultStackItem.MONEY).isPresent());
        inventory.findFirstStackItem(DefaultStackItem.MONEY).ifPresent(m -> assertEquals(20, m.number()));
        assertEquals(inventory.maxCapacity(), inventory.findFirstSlot("药1"));
    }

    @Test
    void size() {
        inventory.add(DefaultStackItem.money(1));
        assertEquals(1, inventory.itemCount());
        assertEquals(inventory.maxCapacity() - 1, inventory.emptySlotSize());
        int emptySlot = inventory.emptySlotSize() - 1;
        for (int i = 0; i < emptySlot; i++) {
            inventory.add(createHair());
        }
        assertEquals(inventory.maxCapacity() - 1, inventory.itemCount());
    }

    @Test
    void decrease() {
        int slot = inventory.add(createHair());
        inventory.decrease(slot);
        assertNull(inventory.getItem(slot));
        slot = inventory.add(DefaultStackItem.builder().type(ItemType.STACK).name("test").number(2).build());
        inventory.decrease(slot);
        assertNotNull(inventory.getItem(slot));
        inventory.decrease(slot);
        assertNull(inventory.getItem(slot));
    }
}
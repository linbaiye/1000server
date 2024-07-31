package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.players.Player;
import org.y1000.item.*;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.trade.TradeItem;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class InventoryTest {

    private Inventory inventory;

    private Player player;

    private ItemSdb itemSdb;

    private final ItemFactory itemFactory = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, new KungFuBookRepositoryImpl());

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
        inventory.add(new WeaponImpl("test", itemSdb));
        Optional<Weapon> weapon = inventory.findWeapon(AttackKungFuType.SWORD);
        assertTrue(weapon.isPresent());
        weapon.ifPresent(w -> assertEquals(w.kungFuType(), AttackKungFuType.SWORD));
    }

    @Test
    void findSlot() {
        inventory.add(new WeaponImpl("test", itemSdb));
        int weaponSlot = inventory.findWeaponSlot(AttackKungFuType.SWORD);
        assertEquals(1, weaponSlot);
        assertEquals(0, inventory.findWeaponSlot(AttackKungFuType.AXE));
    }

    @Test
    void add() {
        assertEquals(0, inventory.add(null));
        assertEquals(1, inventory.add(createHair()));
        assertEquals(2, inventory.add(createHair()));
        assertEquals(3, inventory.add(itemFactory.createMoney(100)));
        assertEquals(3, inventory.add(itemFactory.createMoney(100)));
        for (int i = 0; i < inventory.capacity() - 3; i++) {
            assertNotEquals(0, inventory.add(createHair()));
        }
        assertEquals(0, inventory.add(createHair()));
        assertEquals(0, inventory.add(null));
    }

    @Test
    void consumeStackItemByName() {
        TestingEventListener eventListener = new TestingEventListener();
        assertFalse(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        inventory.add(itemFactory.createItem("箭", 2));
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
    void consumeStackItemByType() {
        inventory = new Inventory();
        TestingEventListener eventListener = new TestingEventListener();
        assertNull( inventory.consumeStackItem(player, ItemType.ARROW, eventListener::onEvent));
        inventory.add(itemFactory.createItem("箭", 1));
        inventory.add(itemFactory.createItem("火箭", 1));
        assertNotNull(inventory.consumeStackItem(player, ItemType.ARROW, eventListener::onEvent));
        UpdateInventorySlotEvent event = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals("", event.toPacket().getUpdateSlot().getName());
        assertFalse(inventory.contains("箭"));
        assertTrue(inventory.contains("火箭"));
        assertNotNull(inventory.consumeStackItem(player, ItemType.ARROW, eventListener::onEvent));
        event = eventListener.dequeue(UpdateInventorySlotEvent.class);
        assertEquals("", event.toPacket().getUpdateSlot().getName());
        assertFalse(event.toPacket().getUpdateSlot().hasNumber());
        assertFalse(inventory.contains("火箭"));
    }

    @Test
    void canSell() {
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1));
        assertFalse(inventory.canSell(items));
        inventory.add(itemFactory.createItem("肉", 2));
        assertTrue(inventory.canSell(items));
        for (int i = 0; i < inventory.capacity() - 1; i++) {
            inventory.add(createHair());
        }
        assertFalse(inventory.canSell(items));
        inventory.remove(inventory.capacity());
        assertTrue(inventory.canSell(items));
        inventory.add(itemFactory.createMoney(1));
        assertTrue(inventory.isFull());
        assertTrue(inventory.canSell(items));
    }

    @Test
    void sell() {
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1));
        inventory.add(itemFactory.createItem("肉", 2));
        var player = Mockito.mock(Player.class);
        inventory.sell(items, itemFactory.createMoney(4), player);
        assertTrue(inventory.findFirstStackItem("肉").isPresent());
        inventory.findFirstStackItem("肉").ifPresent( stackItem -> assertEquals(1, stackItem.number()));
        inventory.findFirstStackItem("钱币").ifPresent(stackItem -> assertEquals(4, stackItem.number()));
    }

    @Test
    void canBuy() {
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 30));
        assertFalse(inventory.canBuy(items, 10));
        inventory.add(itemFactory.createMoney(100));
        assertTrue(inventory.canBuy(items, 10));
        assertFalse(inventory.canBuy(items, 1000));
        int slot = inventory.availableSlots() - 1;
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
        List<TradeItem> items = List.of(new TradeItem("生药", 1, inventory.capacity()));
        inventory.add(itemFactory.createMoney(30));
        var player = Mockito.mock(Player.class);
        inventory.buy(items, 30, player, itemFactory::createItem);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isEmpty());
        assertEquals(inventory.capacity(), inventory.findFirstSlot("生药"));

        inventory = new Inventory();
        inventory.add(itemFactory.createMoney(50));
        inventory.buy(items, 30, player, itemFactory::createItem);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isPresent());
        inventory.findFirstStackItem(ItemType.MONEY_NAME).ifPresent(m -> assertEquals(20, m.number()));
        assertEquals(inventory.capacity(), inventory.findFirstSlot("生药"));
    }

    @Test
    void size() {
        inventory.add(itemFactory.createMoney(1));
        assertEquals(1, inventory.itemCount());
        assertEquals(inventory.capacity() - 1, inventory.availableSlots());
        int emptySlot = inventory.availableSlots() - 1;
        for (int i = 0; i < emptySlot; i++) {
            inventory.add(createHair());
        }
        assertEquals(inventory.capacity() - 1, inventory.itemCount());
    }

    @Test
    void decrease() {
        int slot = inventory.add(createHair());
        inventory.decrease(slot);
        assertNull(inventory.getItem(slot));
        slot = inventory.add(itemFactory.createItem("肉", 2));
        inventory.decrease(slot);
        assertNotNull(inventory.getItem(slot));
        inventory.decrease(slot);
        assertNull(inventory.getItem(slot));
        slot = inventory.add(itemFactory.createItem("肉", 10));
        assertTrue(inventory.decrease(slot, 5));
        assertEquals(5, ((StackItem)inventory.getItem(slot)).number());
        assertTrue(inventory.decrease(slot, 5));
        assertNull(inventory.getItem(slot));
    }

    @Test
    void hasEnough() {
        int slot = inventory.add(createHair());
        assertTrue(inventory.hasEnough(slot, 1));
        assertFalse(inventory.hasEnough(slot, 2));
        slot = inventory.add(itemFactory.createMoney(1000));
        assertTrue(inventory.hasEnough(slot, 1000));
        assertFalse(inventory.hasEnough(slot, 1001));
    }

    @Test
    void canTakeAll() {
        for (int i = 0; i < inventory.capacity() - 2; i++) {
            inventory.add(itemFactory.createItem("长剑"));
        }
        var items = List.of(itemFactory.createItem("生药", 12), itemFactory.createItem("长剑"));
        assertTrue(inventory.canTakeAll(items));
        inventory.add(itemFactory.createItem("生药", 1));
        assertTrue(inventory.canTakeAll(items));
        items = List.of(itemFactory.createItem("长剑"), itemFactory.createItem("长剑"));
        assertFalse(inventory.canTakeAll(items));
        inventory.add(itemFactory.createItem("丸药", 1));
        assertFalse(inventory.canTakeAll(Collections.singletonList(itemFactory.createItem("长刀"))));
        items = List.of(itemFactory.createItem("生药", 12), itemFactory.createItem("丸药", 3));
        assertTrue(inventory.canTakeAll(items));
        assertTrue(inventory.canTakeAll(null));
        assertTrue(inventory.canTakeAll(Collections.emptyList()));
    }
}
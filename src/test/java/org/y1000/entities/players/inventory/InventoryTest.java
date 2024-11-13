package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.players.Player;
import org.y1000.item.*;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.trade.TradeItem;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class InventoryTest extends AbstractUnitTestFixture {

    private Inventory inventory;

    private Player player;

    private ItemSdb itemSdb;

    private final ItemFactory itemFactory = createItemFactory();

    @BeforeEach
    void setUp() {
        itemSdb = Mockito.mock(ItemSdb.class);
        when(itemSdb.getAttackKungFuType(anyString())).thenReturn(AttackKungFuType.SWORD);
        inventory = new Inventory();
        player = Mockito.mock(Player.class);
    }

    private SexualEquipment createHair() {
        return itemFactory.createHair("男子长发");
    }

    @Test
    void findByType() {
        inventory.put(new WeaponImpl("test", itemSdb));
        Optional<Weapon> weapon = inventory.findWeapon(AttackKungFuType.SWORD);
        assertTrue(weapon.isPresent());
        weapon.ifPresent(w -> assertEquals(w.kungFuType(), AttackKungFuType.SWORD));
    }

    @Test
    void findSlot() {
        inventory.put(new WeaponImpl("test", itemSdb));
        int weaponSlot = inventory.findWeaponSlot(AttackKungFuType.SWORD);
        assertEquals(1, weaponSlot);
        assertEquals(0, inventory.findWeaponSlot(AttackKungFuType.AXE));
    }

    @Test
    void add() {
        assertEquals(0, inventory.put(null));
        assertEquals(1, inventory.put(createHair()));
        assertEquals(2, inventory.put(createHair()));
        assertEquals(3, inventory.put(itemFactory.createMoney(100)));
        assertEquals(3, inventory.put(itemFactory.createMoney(100)));
        for (int i = 0; i < inventory.capacity() - 3; i++) {
            assertNotEquals(0, inventory.put(createHair()));
        }
        assertEquals(0, inventory.put(createHair()));
        assertEquals(0, inventory.put(null));
    }

    @Test
    void consumeStackItemByName() {
        TestingEventListener eventListener = new TestingEventListener();
        assertFalse(inventory.consumeStackItem(player, "箭", eventListener::onEvent));
        inventory.put(itemFactory.createItem("箭", 2));
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
        inventory.put(itemFactory.createItem("箭", 1));
        inventory.put(itemFactory.createItem("火箭", 1));
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
        inventory.put(itemFactory.createItem("肉", 2));
        assertTrue(inventory.canSell(items));
        for (int i = 0; i < inventory.capacity() - 1; i++) {
            inventory.put(createHair());
        }
        assertFalse(inventory.canSell(items));
        inventory.remove(inventory.capacity());
        assertTrue(inventory.canSell(items));
        inventory.put(itemFactory.createMoney(1));
        assertTrue(inventory.isFull());
        assertTrue(inventory.canSell(items));
    }

    @Test
    void sell() {
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1));
        inventory.put(itemFactory.createItem("肉", 2));
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
        inventory.put(itemFactory.createMoney(100));
        assertTrue(inventory.canBuy(items, 10));
        assertFalse(inventory.canBuy(items, 1000));
        int slot = inventory.availableSlots() - 1;
        for (int i = 0; i < slot; i++) {
            inventory.put(createHair());
        }
        assertTrue(inventory.canBuy(items, 10));

        // make inventory full.
        inventory.put(createHair());
        assertFalse(inventory.canBuy(items, 10));
    }

    @Test
    void buy() {
        List<TradeItem> items = List.of(new TradeItem("生药", 1, inventory.capacity()));
        inventory.put(itemFactory.createMoney(30));
        var player = Mockito.mock(Player.class);
        inventory.buy(items, 30, player, itemFactory::createItem);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isEmpty());
        assertEquals(inventory.capacity(), inventory.findFirstSlot("生药"));

        inventory = new Inventory();
        inventory.put(itemFactory.createMoney(50));
        inventory.buy(items, 30, player, itemFactory::createItem);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isPresent());
        inventory.findFirstStackItem(ItemType.MONEY_NAME).ifPresent(m -> assertEquals(20, m.number()));
        assertEquals(inventory.capacity(), inventory.findFirstSlot("生药"));
    }

    @Test
    void size() {
        inventory.put(itemFactory.createMoney(1));
        assertEquals(1, inventory.itemCount());
        assertEquals(inventory.capacity() - 1, inventory.availableSlots());
        int emptySlot = inventory.availableSlots() - 1;
        for (int i = 0; i < emptySlot; i++) {
            inventory.put(createHair());
        }
        assertEquals(inventory.capacity() - 1, inventory.itemCount());
    }

    @Test
    void decrease() {
        int slot = inventory.put(createHair());
        inventory.decrease(slot);
        assertNull(inventory.getItem(slot));
        slot = inventory.put(itemFactory.createItem("肉", 2));
        inventory.decrease(slot);
        assertNotNull(inventory.getItem(slot));
        inventory.decrease(slot);
        assertNull(inventory.getItem(slot));
        slot = inventory.put(itemFactory.createItem("肉", 10));
        assertTrue(inventory.decrease(slot, 5));
        assertEquals(5, ((StackItem)inventory.getItem(slot)).number());
        assertTrue(inventory.decrease(slot, 5));
        assertNull(inventory.getItem(slot));
    }

    @Test
    void hasEnough() {
        int slot = inventory.put(createHair());
        assertTrue(inventory.hasEnough(slot, 1));
        assertFalse(inventory.hasEnough(slot, 2));
        slot = inventory.put(itemFactory.createMoney(1000));
        assertTrue(inventory.hasEnough(slot, 1000));
        assertFalse(inventory.hasEnough(slot, 1001));
    }

    @Test
    void canTakeAll() {
        for (int i = 0; i < inventory.capacity() - 2; i++) {
            inventory.put(itemFactory.createItem("长剑"));
        }
        var items = List.of(itemFactory.createItem("生药", 12), itemFactory.createItem("长剑"));
        assertTrue(inventory.canTakeAll(items));
        inventory.put(itemFactory.createItem("生药", 1));
        assertTrue(inventory.canTakeAll(items));
        items = List.of(itemFactory.createItem("长剑"), itemFactory.createItem("长剑"));
        assertFalse(inventory.canTakeAll(items));
        inventory.put(itemFactory.createItem("丸药", 1));
        assertFalse(inventory.canTakeAll(Collections.singletonList(itemFactory.createItem("长刀"))));
        items = List.of(itemFactory.createItem("生药", 12), itemFactory.createItem("丸药", 3));
        assertTrue(inventory.canTakeAll(items));
        assertTrue(inventory.canTakeAll(null));
        assertTrue(inventory.canTakeAll(Collections.emptyList()));
    }

    @Test
    void removeItem() {
        Item item = itemFactory.createItem("生药", 1000);
        int slot = inventory.put(item);
        StackItem removed = (StackItem)inventory.remove(slot, 1);
        assertEquals( "生药", removed.name());
        assertEquals(1, removed.number());
        StackItem item1 = inventory.getItem(slot, StackItem.class).get();
        assertEquals( "生药", item1.name());
        assertEquals( 999, item1.number());
        removed = (StackItem)inventory.remove(slot, 999);
        assertEquals( "生药", removed.name());
        assertEquals(999, removed.number());
        assertNull(inventory.getItem(slot));

        item = itemFactory.createItem("长剑");
        slot = inventory.put(item);
        Item remove = inventory.remove(slot, 1);
        assertEquals("长剑", remove.name());
        assertNull(inventory.getItem(slot));
    }

    @Test
    void put() {
        Item item = itemFactory.createItem("生药", 1000);
        int slot = inventory.put(item);
        inventory.put(slot, itemFactory.createItem("生药", 2));
        StackItem stackItem = inventory.getItem(slot, StackItem.class).get();
        assertEquals(1002, stackItem.number());
        assertEquals("生药", stackItem.name());
    }

    @Test
    void canPut() {
        Item item = itemFactory.createItem("生药", 1000);
        assertFalse(inventory.canPut(0, item));
        assertFalse(inventory.canPut(inventory.capacity() + 1, item));
        assertTrue(inventory.canPut(1, item));
        assertTrue(inventory.canPut(inventory.capacity(), item));
        inventory.put(1, item);
        assertEquals(1000, inventory.getStackItem(1, Pill.class).get().number());
        inventory.put(1, item);
        assertEquals(2000, inventory.getStackItem(1, Pill.class).get().number());
    }

    @Test
    void hasEnoughByNameAndNumber() {
        Item item = itemFactory.createItem("生药", 1000);
        inventory.put(item);
        assertTrue(inventory.hasEnough("生药", 999));
        assertFalse(inventory.hasEnough("生药", 1001));
        var rs = itemFactory.createItem("锈剑", 1);
        inventory.put(rs);
        assertTrue(inventory.hasEnough("锈剑", 1));
        assertFalse(inventory.hasEnough("锈剑", 0));
        assertFalse(inventory.hasEnough("锈剑", 2));
    }

    @Test
    void consumeByNameAndNumber() {
        Item item = itemFactory.createItem("生药", 1000);
        assertEquals(0, inventory.consume("生药", 1001));
        int slot = inventory.put(item);
        assertEquals(slot, inventory.consume("生药", 999));

        var rs = itemFactory.createItem("锈剑", 1);
        var slot1 = inventory.put(rs);
        assertEquals(slot1, inventory.consume("锈剑", 1));
        assertNull(inventory.getItem(slot1));
    }
}
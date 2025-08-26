package org.y1000.entities.players.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.equipment.SexualEquipment;
import org.y1000.entities.players.equipment.Weapon;
import org.y1000.entities.players.equipment.WeaponImpl;
import org.y1000.item.*;
import org.y1000.kungfu.attack.AttackKungFuType;

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
    void removeItem() {
        Item item = itemFactory.createItem("生药", 1000);
        int slot = inventory.add(item);
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
        slot = inventory.add(item);
        Item remove = inventory.remove(slot, 1);
        assertEquals("长剑", remove.name());
        assertNull(inventory.getItem(slot));
    }


    @Test
    void canAdd() {
        Item item = itemFactory.createItem("生药", 1000);
        assertFalse(inventory.canAdd(0, item));
        assertFalse(inventory.canAdd(inventory.capacity() + 1, item));
        assertTrue(inventory.canAdd(1, item));
        assertTrue(inventory.canAdd(inventory.capacity(), item));
        inventory.add(1, item);
        assertEquals(1000, inventory.getStackItem(1, Pill.class).get().number());
        inventory.add(1, item);
        assertEquals(2000, inventory.getStackItem(1, Pill.class).get().number());
    }

    @Test
    void hasEnoughByNameAndNumber() {
        Item item = itemFactory.createItem("生药", 1000);
        inventory.add(item);
        assertTrue(inventory.hasEnough("生药", 999));
        assertFalse(inventory.hasEnough("生药", 1001));
        var rs = itemFactory.createItem("锈剑", 1);
        inventory.add(rs);
        assertTrue(inventory.hasEnough("锈剑", 1));
        assertFalse(inventory.hasEnough("锈剑", 0));
        assertFalse(inventory.hasEnough("锈剑", 2));
    }

    @Test
    void decreaseByNameAndNumber() {
        Item item = itemFactory.createItem("生药", 1000);
        assertEquals(0, inventory.decrease("生药", 1001));
        int slot = inventory.add(item);
        assertEquals(slot, inventory.decrease("生药", 999));

        var rs = itemFactory.createItem("锈剑", 1);
        var from = inventory.add(rs);
        assertEquals(from, inventory.decrease("锈剑", 1));
        assertNull(inventory.getItem(from));
    }

    @Test
    void moveToMergable() {
        Item item = itemFactory.createItem("生药", 1000);
        int slot = inventory.add(item);
        item = itemFactory.createItem("生药", 1000);
        inventory.add(slot + 1, item);
        boolean move = inventory.move(slot + 1, slot);
        assertTrue(move);
        assertNull(inventory.getItem(slot + 1));
        Optional<StackItem> item1 = inventory.getItem(slot, StackItem.class);
        assertTrue(item1.isPresent());
        assertEquals(2000, item1.get().number());
    }

    @Test
    void moveFromEmpty() {
        Item item = itemFactory.createItem("生药", 1000);
        int slot = inventory.add(item);
        inventory.move(slot + 1, slot);
        assertEquals("生药", inventory.getItem(slot).name());
        assertNull(inventory.getItem(slot + 1));
    }

    @Test
    void moveToNonMergable() {
        Item item = itemFactory.createItem("生药", 1000);
        int slot = inventory.add(item);
        Item item1 = itemFactory.createItem("丸药", 1000);
        int slot1 = inventory.add(item1);
        inventory.move(slot, slot1);
        Optional<StackItem> item2 = inventory.getItem(slot, StackItem.class);
        assertTrue(item2.isPresent());
        assertEquals("丸药", item2.get().name());
        assertEquals(1000, item2.get().number());
        Optional<StackItem> item3 = inventory.getItem(slot1, StackItem.class);
        assertTrue(item3.isPresent());
        assertEquals("生药", item3.get().name());
    }
}
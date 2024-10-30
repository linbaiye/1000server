package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.*;
import org.y1000.persistence.EquipmentPo;
import org.y1000.persistence.InventoryPo;
import org.y1000.persistence.PlayerItemPo;
import org.y1000.persistence.SlotItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryImplTest extends AbstractUnitTestFixture {

    private ItemFactory itemFactory;

    private ItemRepository itemRepository;

    private JpaFixture jpaFixture;

    @BeforeEach
    void setUp() {
        itemFactory = createItemFactory();
        itemRepository = createItemRepository();
        jpaFixture = new JpaFixture();
    }

    @Test
    void createAmmo() {
        Item item = itemFactory.createItem("箭", 2);
        assertEquals(ItemType.ARROW, item.itemType());
        assertTrue(((Ammo)((StackItem)item).item()).spriteId() != 0);
        item = itemFactory.createItem("飞刀", 2);
        assertEquals(ItemType.KNIFE, item.itemType());
    }


    @Test
    void saveEmptyInventory() {
        PlayerImpl player = playerBuilder().id(11).build();
        Inventory inventory = player.inventory();
        EntityManager entityManager = jpaFixture.beginTx();
        itemRepository.saveInventory(entityManager, player.id(), inventory);
        jpaFixture.submitTx();
        entityManager = jpaFixture.newEntityManager();
        InventoryPo inventoryPo = entityManager.createQuery("select i from InventoryPo i where i.playerId = ?1", InventoryPo.class)
                .setParameter(1, player.id())
                .getResultList().get(0);
        assertTrue(inventoryPo.getSlots().isEmpty());
    }

    @Test
    void saveInventory() {
        PlayerImpl player = playerBuilder().id(11).build();
        Inventory inventory = player.inventory();
        int slot0 = inventory.put(itemFactory.createItem("生药", 1));
        int slot1 = inventory.put(itemFactory.createItem("丸药", 2));
        int slot2 = inventory.put(itemFactory.createItem("长剑", 1));
        EntityManager entityManager = jpaFixture.beginTx();
        itemRepository.saveInventory(entityManager, player.id(), inventory);
        jpaFixture.submitTx();
        entityManager = jpaFixture.newEntityManager();
        InventoryPo inventoryPo = entityManager.createQuery("select i from InventoryPo i where i.playerId = ?1", InventoryPo.class)
                .setParameter(1, player.id())
                .getResultList().get(0);
        assertTrue(inventoryPo.getSlots().stream().map(SlotItem::getSlot).collect(Collectors.toSet()).containsAll(Set.of(slot0, slot1, slot2)));
        assertNotNull(inventory.getItem(slot2, Equipment.class).get().id());
        assertFalse(entityManager.createQuery("select e from EquipmentPo e where id = ?1")
                .setParameter(1, inventory.getItem(slot2, Equipment.class).get().id())
                .getResultList().isEmpty());
    }

    @Test
    void findInventory() {
        PlayerImpl player = playerBuilder().id(11).build();
        Inventory inventory = player.inventory();
        int slot1 = inventory.put(itemFactory.createItem("生药", 2));
        Equipment equipment = itemFactory.createEquipment("女子黄龙弓服", 4);
        equipment.findAbility(Upgradable.class).get().upgrade();
        int slot2 = inventory.put(equipment);
        EntityManager entityManager = jpaFixture.beginTx();
        itemRepository.saveInventory(entityManager, player.id(), inventory);
        jpaFixture.submitTx();
        Inventory loaded = itemRepository.findInventory(entityManager, player.id()).get();
        StackItem stackItem = loaded.getStackItem(slot1, Pill.class).get();
        assertEquals(2, stackItem.number());
        assertEquals("生药", stackItem.name());
        Equipment loadedEquipment = loaded.getItem(slot2, Equipment.class).get();
        assertEquals(1, loadedEquipment.findAbility(Upgradable.class).get().level());
    }
}
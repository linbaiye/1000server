package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.*;
import org.y1000.persistence.ItemPo;

import java.util.List;

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
    void findInventory() {
        PlayerImpl player = playerBuilder().id(11).build();
        Inventory inventory = player.inventory();
        int slot1 = inventory.put(itemFactory.createItem("丸药", 2));
        DecorativeEquipment equipment = (DecorativeEquipment) itemFactory.createItem("女子长发", 1);
        StackItem dye = (StackItem) itemFactory.createItem("天蓝染剂", 1);
        equipment.dye(dye.color());
        int slot2 = inventory.put(equipment);
        EntityManager entityManager = jpaFixture.beginTx();
        itemRepository.save(entityManager, player);
        jpaFixture.submitTx();

        Inventory inv = itemRepository.findInventory(jpaFixture.newEntityManager(), player.id());
        assertEquals("丸药", inv.getItem(slot1).name());
        assertEquals(2, ((StackItem)inv.getItem(slot1)).number());
        assertEquals("女子长发", inv.getItem(slot2).name());
        assertEquals(equipment.color(), inv.getItem(slot2).color());
    }


    @Test
    void save() {
        PlayerImpl player = playerBuilder().id(11).build();
        Inventory inventory = player.inventory();
        inventory.put(itemFactory.createItem("生药", 1));
        int slot1 = inventory.put(itemFactory.createItem("丸药", 2));
        int slot2 = inventory.put(itemFactory.createItem("长剑", 1));
        EntityManager entityManager = jpaFixture.beginTx();
        itemRepository.save(entityManager, player);
        jpaFixture.submitTx();
        try (var em = jpaFixture.newEntityManager()) {
            List<ItemPo> resultList = em.createQuery("select i from ItemPo i where i.itemKey.playerId = ?1", ItemPo.class)
                    .setParameter(1, player.id())
                    .getResultList();
            assertEquals(player.id(), resultList.get(0).getItemKey().getPlayerId());
            List<String> names = resultList.stream().map(ItemPo::getName).toList();
            assertTrue(names.contains("生药"));
            assertTrue(names.contains("丸药"));
            assertTrue(names.contains("长剑"));
            ItemPo itemPo = resultList.stream().filter(i -> i.getName().equals("丸药")).findFirst().get();
            assertEquals(2, itemPo.getNumber());
            assertEquals(slot1, itemPo.getItemKey().getSlot());
            itemPo = resultList.stream().filter(i -> i.getName().equals("长剑")).findFirst().get();
            assertEquals(slot2, itemPo.getItemKey().getSlot());
        }



    }
}
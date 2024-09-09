package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.*;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.persistence.ItemPo;
import org.y1000.sdb.ItemDrugSdbImpl;

import java.util.List;
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
    void save() {
        PlayerImpl player = playerBuilder().build();
        Inventory inventory = player.inventory();
        inventory.add(itemFactory.createItem("生药", 1));
        inventory.add(itemFactory.createItem("丸药", 1));
        EntityManager entityManager = jpaFixture.beginTx();
        itemRepository.save(entityManager, player);
        jpaFixture.submitTx();
        try (var em = jpaFixture.newEntityManager()) {
            List<String> names = em.createQuery("select i from ItemPo i where i.itemKey.playerId = ?1", ItemPo.class)
                    .setParameter(1, player.id())
                    .getResultStream().map(ItemPo::getName)
                    .toList();
            assertTrue(names.contains("生药"));
            assertTrue(names.contains("丸药"));
        }
    }
}
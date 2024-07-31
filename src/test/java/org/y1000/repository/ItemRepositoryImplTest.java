package org.y1000.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.item.*;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.sdb.ItemDrugSdbImpl;

import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryImplTest {

    private ItemFactory itemFactory;

    @BeforeEach
    void setUp() {
        KungFuFactory kungFuFactory = Mockito.mock(KungFuFactory.class);
        itemFactory = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, kungFuFactory);
    }

    @Test
    void createAmmo() {
        Item item = itemFactory.createItem("箭", 2);
        assertEquals(ItemType.ARROW, item.itemType());
        assertTrue(((Ammo)((StackItem)item).item()).spriteId() != 0);
        item = itemFactory.createItem("飞刀", 2);
        assertEquals(ItemType.KNIFE, item.itemType());
    }
}
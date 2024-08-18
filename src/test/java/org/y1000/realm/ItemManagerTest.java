package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEntityEventSender;
import org.y1000.TestingEventListener;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.item.ItemSdbImpl;
import org.y1000.message.PlayerTextEvent;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.util.Coordinate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class ItemManagerTest extends AbstractUnitTestFixture {

    private ItemManagerImpl manager;

    private TestingEntityEventSender eventSender;

    private ItemSdb itemSdb;

    private ItemFactory itemFactory;

    @BeforeEach
    void setUp() {
        eventSender = new TestingEntityEventSender();
        itemSdb = Mockito.mock(ItemSdb.class);
        itemFactory = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, new KungFuBookRepositoryImpl());
        manager = new ItemManagerImpl(eventSender, itemSdb, new EntityIdGenerator(), itemFactory);
    }

    @Test
    void pickItem() {
        GroundedItem groundItem = GroundedItem.builder().name("肉").number(2).id(3).coordinate(Coordinate.xy(1, 2)).build();
        TestingEventListener itemEvenListener = new TestingEventListener();
        groundItem.registerEventListener(itemEvenListener);
        manager.add(groundItem);
        var picker = playerBuilder().coordinate(Coordinate.xy(5, 2)).build();
        picker.joinRealm(mockAllFlatRealm());
        TestingEventListener playerEventListener = new TestingEventListener();
        picker.registerEventListener(playerEventListener);
        manager.pickItem(picker, 3);
        PlayerTextEvent textEvent = playerEventListener.removeFirst(PlayerTextEvent.class);
        // when too far.
        assertEquals(PlayerTextEvent.TextType.FARAWAY.value(), textEvent.toPacket().getText().getType());

        picker.changeCoordinate(Coordinate.xy(2, 2));
        manager.pickItem(picker, 3);
        Item item = picker.inventory().getItem(1);
        assertEquals("肉", item.name());
        assertTrue(manager.find(3).isEmpty());
        assertNotNull(eventSender.removeFirst(groundItem, RemoveEntityEvent.class));
    }

    @Test
    void dropItem() {
        manager.dropItem("皮:2:1:肉:5:1", Coordinate.xy(1, 1));
        Optional<GroundedItem> groundedItem = manager.find(1L);
        assertEquals("皮", groundedItem.get().getName());
        assertEquals(2, groundedItem.get().getNumber());
        assertEquals(Coordinate.xy(1, 1), groundedItem.get().coordinate());
        groundedItem = manager.find(2L);
        assertEquals("肉", groundedItem.get().getName());
        assertEquals(5, groundedItem.get().getNumber());

        manager.dropItem("钱币",  100, Coordinate.xy(2, 2));
        groundedItem = manager.find(3L);
        assertEquals("钱币", groundedItem.get().getName());
        assertEquals(100, groundedItem.get().getNumber());
        assertEquals(Coordinate.xy(2, 2), groundedItem.get().coordinate());
    }

}
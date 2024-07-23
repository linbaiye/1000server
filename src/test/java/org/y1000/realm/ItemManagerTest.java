package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEntityEventSender;
import org.y1000.TestingEventListener;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.item.ItemSdbImpl;
import org.y1000.message.PlayerTextEvent;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.sdb.MonstersSdb;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemManagerTest extends AbstractUnitTestFixture {

    private ItemManagerImpl manager;

    private TestingEntityEventSender eventSender;

    private MonstersSdb monstersSdb;

    private ItemSdb itemSdb;

    private ItemFactory itemFactory;

    @BeforeEach
    void setUp() {
        eventSender = new TestingEntityEventSender();
        monstersSdb = Mockito.mock(MonstersSdb.class);
        itemSdb = Mockito.mock(ItemSdb.class);
        itemFactory = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, new KungFuBookRepositoryImpl());
        manager = new ItemManagerImpl(eventSender, itemSdb, monstersSdb, new EntityIdGenerator(), itemFactory);
    }


    @Test
    void whenAMonsterDead() {
        when(itemSdb.getSoundDrop("肉")).thenReturn("dropSound");
        when(monstersSdb.getHaveItem(any(String.class))).thenReturn("皮:2:1:肉:4:1");
        TestingMonsterAttributeProvider attributeProvider = new TestingMonsterAttributeProvider();
        attributeProvider.idName = "牛";
        manager.onEvent(new CreatureDieEvent(monsterBuilder().attributeProvider(attributeProvider).name("牛").build()));
        assertEquals(2, eventSender.entities().size());
        EntitySoundEvent soundEvent = eventSender.removeFirst(EntitySoundEvent.class);
        assertEquals("dropSound", soundEvent.toPacket().getSound().getSound());
    }

    @Test
    void pickItem() {
        GroundedItem groundItem = GroundedItem.builder().name("肉").number(2).id(3).coordinate(Coordinate.xy(1, 2)).build();
        TestingEventListener itemEvenListener = new TestingEventListener();
        groundItem.registerEventListener(itemEvenListener);
        manager.add(groundItem);
        var picker = playerBuilder().coordinate(Coordinate.xy(5, 2)).build();
        picker.joinReam(mockAllFlatRealm());
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
        assertNotNull(itemEvenListener.removeFirst(RemoveEntityEvent.class));
    }
}
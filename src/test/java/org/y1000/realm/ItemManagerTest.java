package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEntityEventSender;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.item.ItemSdb;
import org.y1000.message.serverevent.ShowItemEvent;
import org.y1000.sdb.MonstersSdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemManagerTest extends AbstractUnitTestFixture {

    private ItemManager manager;

    private TestingEntityEventSender eventSender;

    private MonstersSdb monstersSdb;

    private ItemSdb itemSdb;

    @BeforeEach
    void setUp() {
        eventSender = new TestingEntityEventSender();
        monstersSdb = Mockito.mock(MonstersSdb.class);
        itemSdb = Mockito.mock(ItemSdb.class);
        manager = new ItemManager(eventSender, itemSdb, monstersSdb, new EntityIdGenerator());
    }


    @Test
    void whenAMonsterDead() {
        when(itemSdb.getSoundDrop("肉")).thenReturn("dropSound");
        when(monstersSdb.getHaveItem(any(String.class))).thenReturn("皮:2:1:肉:4:1");
        manager.onEvent(new CreatureDieEvent(monsterBuilder().name("牛").build()));
        assertEquals(2, eventSender.entities().size());
        EntitySoundEvent soundEvent = eventSender.removeFirst(EntitySoundEvent.class);
        assertEquals("dropSound", soundEvent.toPacket().getSound().getSound());
    }
}
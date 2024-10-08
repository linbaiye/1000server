package org.y1000.quest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.event.EntityEvent;
import org.y1000.item.ItemFactory;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.sdb.QuestSdb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class QuestTest extends AbstractUnitTestFixture {

    private QuestSdb questSdb;
    private final ItemFactory itemFactory = createItemFactory();
    private Player player;

    private Inventory inventory;

    private TestingEventListener eventListener;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        player = Mockito.mock(Player.class);
        eventListener = new TestingEventListener();
        doAnswer(invocationOnMock -> {
            eventListener.onEvent(invocationOnMock.getArgument(0));
            return null;
        }).when(player).emitEvent(any(EntityEvent.class));
        when(player.inventory()).thenReturn(inventory);
        questSdb = Mockito.mock(QuestSdb.class);
        when(questSdb.getRequiredItems(anyString())).thenReturn("生药:1|丸药:1");
        when(questSdb.getReward(anyString())).thenReturn("长剑:1");
        when(questSdb.getSubmitText(anyString())).thenReturn("submit");
        when(questSdb.getDescription(anyString())).thenReturn("description");
    }

    @Test
    void parse() {
        Quest quest = Quest.parse("test", questSdb);
        assertEquals("description", quest.getDescription());
        assertEquals("submit", quest.getSubmitText());
    }

    @Test
    void canComplete() {
        inventory.put(itemFactory.createItem("生药", 1));
        Quest quest = Quest.parse("test", questSdb);
        assertTrue(quest.canComplete(player).contains("需要1个丸药"));
        inventory.put(itemFactory.createItem("丸药", 1));
        assertNull(quest.canComplete(player));
        while(!inventory.isFull()) {
            inventory.put(itemFactory.createItem("长剑"));
        }
        assertTrue(quest.canComplete(player).contains("物品栏已满"));
    }

    @Test
    void complete() {
        inventory.put(itemFactory.createItem("生药", 1));
        Quest quest = Quest.parse("test", questSdb);
        inventory.put(itemFactory.createItem("丸药", 1));
        quest.complete(player, itemFactory::createItem);
        assertEquals("长剑", inventory.getItem(1).name());
        assertNull(inventory.getItem(2));
        assertNotNull(eventListener.removeFirst(UpdateInventorySlotEvent.class));
        assertNotNull(eventListener.removeFirst(UpdateInventorySlotEvent.class));
        assertNotNull(eventListener.removeFirst(UpdateInventorySlotEvent.class));
        assertNotNull(eventListener.removeFirst(PlayerTextEvent.class));
        assertNotNull(eventListener.removeFirst(EntitySoundEvent.class));
    }
}
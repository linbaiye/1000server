package org.y1000.message.clientevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.npc.Quester;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.event.EntityEvent;
import org.y1000.item.ItemFactory;
import org.y1000.message.serverevent.EntityChatEvent;
import org.y1000.quest.Quest;
import org.y1000.sdb.QuestSdb;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ClientSubmitQuestEventTest extends AbstractUnitTestFixture {

    private QuestSdb questSdb;
    private final ItemFactory itemFactory = createItemFactory();
    private Player player;

    private Inventory inventory;

    private TestingEventListener eventListener;
    private Quest quest;

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
        quest = Quest.parse("test", questSdb);
    }

    @Test
    void whenRequiredItemsNotFulfill() {
        Quester quester = Mockito.mock(Quester.class);
        when(quester.getQuest()).thenReturn(quest);
        when(quester.id()).thenReturn(1L);
        ClientSubmitQuestEvent event = new ClientSubmitQuestEvent(quester.id(), quest.getQuestName(), itemFactory);
        event.handle(player, quester);
        verify(quester, times(1)).emitEvent(any(EntityChatEvent.class));
    }
}
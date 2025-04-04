package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.entities.players.event.OpenTradeWindowEvent;
import org.y1000.entities.players.event.UpdateTradeWindowEvent;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.StackItem;
import org.y1000.message.PlayerMoveEvent;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.TextMessage;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.network.gen.TextMessagePacket;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeManagerImplTest extends AbstractUnitTestFixture {
    private TradeManager tradeManager;
    private Inventory traderInventory;
    private Inventory tradeeInventory;
    private Player trader;
    private Player tradee;
    private ItemFactory itemFactory;

    private TestingEventListener traderEventListener;
    private TestingEventListener tradeeEventListener;
    private EntityEventSender eventSender;


    @BeforeEach
    void setUp() {
        eventSender = Mockito.mock(EntityEventSender.class);
        itemFactory = createItemFactory();
        trader = Mockito.mock(Player.class);
        tradeManager = new TradeManagerImpl(eventSender);
        traderInventory = new Inventory();
        traderInventory.put(itemFactory.createItem("长剑"));
        when(trader.inventory()).thenReturn(traderInventory);
        when(trader.coordinate()).thenReturn(Coordinate.xy(1, 1));

        tradee = Mockito.mock(Player.class);
        tradeeInventory = new Inventory();
        when(tradee.inventory()).thenReturn(tradeeInventory);
        when(tradee.coordinate()).thenReturn(Coordinate.xy(2, 2));
        tradeeEventListener = new TestingEventListener();
        traderEventListener = new TestingEventListener();
        doAnswer(invocationOnMock -> {
            AbstractPlayerEvent event = invocationOnMock.getArgument(0);
            if (event.source() == trader) {
                traderEventListener.onEvent(event);
            } else if (event.source() == tradee) {
                tradeeEventListener.onEvent(event);
            }
            return null;
        }).when(eventSender).notifySelf(any(AbstractPlayerEvent.class));
    }

    @Test
    void start() {
        Inventory inventory = new Inventory();
        var slot = inventory.put(Mockito.mock(Item.class));
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.inventory()).thenReturn(inventory);
        tradeManager.start(trader, tradee, slot);
        verify(eventSender, times(2)).notifySelf(any(OpenTradeWindowEvent.class));
        assertNotNull(tradeeEventListener.removeFirst(OpenTradeWindowEvent.class));
        assertNotNull(traderEventListener.removeFirst(OpenTradeWindowEvent.class));
        assertTrue(tradeeEventListener.isEmpty());
        assertTrue(traderEventListener.isEmpty());
    }

    @Test
    void startDoesNothingWhenSamePlayer() {
        Inventory inventory = new Inventory();
        var slot = inventory.put(Mockito.mock(Item.class));
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.inventory()).thenReturn(inventory);
        tradeManager.start(trader, trader, slot);
        assertTrue(tradeeEventListener.isEmpty());
        assertTrue(traderEventListener.isEmpty());
    }

    @Test
    void startWhenTradeDisabled() {
        Inventory inventory = new Inventory();
        var slot = inventory.put(Mockito.mock(Item.class));
        when(tradee.tradeEnabled()).thenReturn(false);
        when(trader.inventory()).thenReturn(inventory);
        tradeManager.start(trader, tradee, slot);
        TextMessagePacket text = traderEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText();
        assertEquals(TextMessage.TextType.TRADE_REJECTED.value(), text.getType());
        assertTrue(tradeeEventListener.isEmpty());
        assertTrue(traderEventListener.isEmpty());
    }

    @Test
    void startWhenTrading() {
        Inventory inventory = new Inventory();
        var slot = inventory.put(Mockito.mock(Item.class));
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.inventory()).thenReturn(inventory);
        var tradingTrader = Mockito.mock(Player.class);
        inventory = new Inventory();
        slot = inventory.put(Mockito.mock(Item.class));
        when(tradingTrader.inventory()).thenReturn(inventory);
        when(tradingTrader.coordinate()).thenReturn(Coordinate.xy(2, 3));
        tradeManager.start(tradingTrader,tradee, slot);
        assertNotNull(tradeeEventListener.removeFirst(OpenTradeWindowEvent.class));

        tradeManager.start(trader, tradee, slot);
        TextMessagePacket text = traderEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText();
        assertEquals(TextMessage.TextType.MULTI_TRADE.value(), text.getType());
    }

    @Test
    void whenPlayerMovedFar() {
        when(tradee.tradeEnabled()).thenReturn(true);
        tradeManager.start(trader, tradee, 1);
        tradeManager.onPlayerEvent(trader, new PlayerMoveEvent(trader, Direction.DOWN, Coordinate.xy(1, 2)));
        tradeManager.addTradeItem(trader, 1, 1);
        assertNotNull(tradeeEventListener.removeFirst(UpdateTradeWindowEvent.class));
        assertNotNull(traderEventListener.removeFirst(UpdateTradeWindowEvent.class));
        assertNull(trader.inventory().getItem(1));
        when(trader.coordinate()).thenReturn(Coordinate.xy(5, 2));
        tradeManager.onPlayerEvent(trader, new PlayerMoveEvent(trader, Direction.DOWN, Coordinate.xy(5, 3)));
        assertNotNull(trader.inventory().getItem(1));
        assertNotNull(tradeeEventListener.removeFirst(UpdateTradeWindowEvent.class));
        assertNotNull(traderEventListener.removeFirst(UpdateTradeWindowEvent.class));
    }

    @Test
    void addItem() {
        when(tradee.tradeEnabled()).thenReturn(true);
        tradeManager.start(trader, tradee, 1);
        tradeManager.addTradeItem(trader, 1, 1);
        assertNull(traderInventory.getItem(1));
        assertNotNull(traderEventListener.removeFirst(UpdateInventorySlotEvent.class));
        var addItemEvent = traderEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertTrue(addItemEvent.toPacket().getUpdateTradeWindow().getSelf());
        assertEquals(1, addItemEvent.toPacket().getUpdateTradeWindow().getNumber());
        addItemEvent = tradeeEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertFalse(addItemEvent.toPacket().getUpdateTradeWindow().getSelf());
    }

    @Test
    void removeItem() {
        when(tradee.tradeEnabled()).thenReturn(true);
        tradeManager.start(trader, tradee, 1);
        tradeManager.addTradeItem(trader, 1, 1);
        traderEventListener.clearEvents();
        tradeeEventListener.clearEvents();
        tradeManager.removeTradeItem(trader, 1);;
        var removeEvent = tradeeEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertEquals(UpdateTradeWindowEvent.Type.REMOVE_ITEM.value(), removeEvent.toPacket().getUpdateTradeWindow().getType());
        assertEquals(1, removeEvent.toPacket().getUpdateTradeWindow().getSlot());
        assertFalse(removeEvent.toPacket().getUpdateTradeWindow().getSelf());
        assertNotNull(traderEventListener.removeFirst(UpdateInventorySlotEvent.class));
        removeEvent = traderEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertTrue(removeEvent.toPacket().getUpdateTradeWindow().getSelf());
    }

    @Test
    void cancel() {
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.tradeEnabled()).thenReturn(true);
        tradeManager.start(trader, tradee, 1);
        tradeManager.addTradeItem(trader, 1, 1);
        var tradeeSlot = tradeeInventory.put(itemFactory.createItem("生药", 3));
        tradeManager.addTradeItem(tradee, tradeeSlot, 1);
        assertEquals(2, ((StackItem)tradeeInventory.getItem(tradeeSlot)).number());
        assertNull(traderInventory.getItem(1));
        tradeManager.cancelTrade(trader);
        assertNotNull(traderInventory.getItem(1));
        assertEquals(3, ((StackItem)tradeeInventory.getItem(tradeeSlot)).number());
    }

    @Test
    void confirmWhenFull() {
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.tradeEnabled()).thenReturn(true);
        int slots = traderInventory.availableSlots();
        for (int i = 0; i < slots; i++) {
            traderInventory.put(itemFactory.createItem("长刀"));
        }
        tradeManager.start(trader, tradee, 1);
        tradeManager.addTradeItem(trader, 1, 1);
        var tradeeSlot = tradeeInventory.put(itemFactory.createItem("生药", 3));
        tradeManager.addTradeItem(tradee, tradeeSlot, 1);
        tradeeSlot = tradeeInventory.put(itemFactory.createItem("丸药", 2));
        tradeManager.addTradeItem(tradee, tradeeSlot, 1);
        traderEventListener.clearEvents();
        tradeeEventListener.clearEvents();
        // end setup.

        tradeManager.confirmTrade(trader);
        tradeManager.confirmTrade(tradee);
        var update = traderEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertEquals(UpdateTradeWindowEvent.Type.CLOSE_WINDOW.value(), update.toPacket().getUpdateTradeWindow().getType());
        update = tradeeEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertEquals(UpdateTradeWindowEvent.Type.CLOSE_WINDOW.value(), update.toPacket().getUpdateTradeWindow().getType());
        assertEquals("长剑", trader.inventory().getItem(1).name());
        assertEquals(3, ((StackItem)tradee.inventory().getItem(1)).number());
        assertEquals(2, ((StackItem)tradee.inventory().getItem(2)).number());
    }

    @Test
    void confirmWhenEmpty() {
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.tradeEnabled()).thenReturn(true);
        tradeManager.start(trader, tradee, 1);
        tradeManager.removeTradeItem(trader,  1);
        traderEventListener.clearEvents();
        tradeeEventListener.clearEvents();
        tradeManager.confirmTrade(trader);
        tradeManager.confirmTrade(tradee);
        assertNull(traderEventListener.removeFirst(UpdateInventorySlotEvent.class));
        assertNull(tradeeEventListener.removeFirst(UpdateInventorySlotEvent.class));
        UpdateTradeWindowEvent update = traderEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertEquals(UpdateTradeWindowEvent.Type.CLOSE_WINDOW.value(), update.toPacket().getUpdateTradeWindow().getType());
        update = tradeeEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertEquals(UpdateTradeWindowEvent.Type.CLOSE_WINDOW.value(), update.toPacket().getUpdateTradeWindow().getType());
    }

    @Test
    void confirmToSwap() {
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.tradeEnabled()).thenReturn(true);
        tradeManager.start(trader, tradee, 1);
        tradeManager.addTradeItem(trader, 1, 1);
        tradeeInventory.put(itemFactory.createItem("生药", 3));
        tradeManager.addTradeItem(tradee, 1, 1);
        traderEventListener.clearEvents();
        tradeeEventListener.clearEvents();
        tradeManager.confirmTrade(trader);
        tradeManager.confirmTrade(tradee);
        UpdateTradeWindowEvent update = traderEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertEquals(UpdateTradeWindowEvent.Type.CLOSE_WINDOW.value(), update.toPacket().getUpdateTradeWindow().getType());
        update = tradeeEventListener.removeFirst(UpdateTradeWindowEvent.class);
        assertEquals(UpdateTradeWindowEvent.Type.CLOSE_WINDOW.value(), update.toPacket().getUpdateTradeWindow().getType());
        assertNotNull(traderEventListener.removeFirst(UpdateInventorySlotEvent.class));
        assertNotNull(tradeeEventListener.removeFirst(UpdateInventorySlotEvent.class));
        assertNotEquals(0, tradee.inventory().findFirstSlot("长剑"));
        assertNotEquals(0, trader.inventory().findFirstSlot("生药"));
    }
}
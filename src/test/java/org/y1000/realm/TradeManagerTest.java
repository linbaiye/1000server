package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.OpenTradeWindowEvent;
import org.y1000.entities.players.event.UpdateTradeWindowEvent;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.event.EntityEvent;
import org.y1000.item.DefaultItem;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdbImpl;
import org.y1000.message.PlayerMoveEvent;
import org.y1000.message.PlayerTextEvent;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.util.Coordinate;

import static org.mockito.Mockito.*;

class TradeManagerTest {
    private TradeManager tradeManager;
    private Inventory traderInventory;
    private Player trader;
    private Player tradee;
    private ItemFactory itemFactory;

    @BeforeEach
    void setUp() {
        itemFactory = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, new KungFuBookRepositoryImpl());
        tradeManager = new TradeManager(itemFactory);
        traderInventory = new Inventory();
        traderInventory.add(DefaultItem.builder().name("test").desc("test").eventSound("").dropSound("").build());
        trader = Mockito.mock(Player.class);
        when(trader.coordinate()).thenReturn(Coordinate.xy(1, 1));
        tradee = Mockito.mock(Player.class);
        when(tradee.coordinate()).thenReturn(Coordinate.xy(2, 2));
        when(trader.inventory()).thenReturn(traderInventory);
    }

    @Test
    void start() {
        Inventory inventory = new Inventory();
        var slot = inventory.add(DefaultItem.builder().name("test").desc("test").eventSound("").dropSound("").build());
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.inventory()).thenReturn(inventory);
        tradeManager.start(trader, tradee, slot);
        verify(trader, times(1)).emitEvent(any(OpenTradeWindowEvent.class));
        verify(tradee, times(1)).emitEvent(any(OpenTradeWindowEvent.class));
    }

    @Test
    void startWhenTradeDisabled() {
        Inventory inventory = new Inventory();
        var slot = inventory.add(DefaultItem.builder().name("test").desc("test").eventSound("").dropSound("").build());
        when(tradee.tradeEnabled()).thenReturn(false);
        when(trader.inventory()).thenReturn(inventory);
        tradeManager.start(trader, tradee, slot);
        verify(trader, times(1)).emitEvent(any(PlayerTextEvent.class));
        verify(tradee, times(0)).emitEvent(any(EntityEvent.class));
    }

    @Test
    void startWhenTrading() {
        Inventory inventory = new Inventory();
        var slot = inventory.add(DefaultItem.builder().name("test").desc("test").eventSound("").dropSound("").build());
        when(tradee.tradeEnabled()).thenReturn(true);
        when(trader.inventory()).thenReturn(inventory);
        tradeManager.start(trader, tradee, slot);
        var trader2 = Mockito.mock(Player.class);
        inventory = new Inventory();
        slot = inventory.add(DefaultItem.builder().name("test").desc("test").eventSound("").dropSound("").build());
        when(trader2.inventory()).thenReturn(inventory);
        when(trader2.coordinate()).thenReturn(Coordinate.xy(2, 3));
        tradeManager.start(trader2, tradee, slot);
        verify(trader2, times(1)).emitEvent(any(PlayerTextEvent.class));
        verify(tradee, times(1)).emitEvent(any(EntityEvent.class));
    }

    @Test
    void whenPlayerMovedFar() {
        when(tradee.tradeEnabled()).thenReturn(true);
        tradeManager.start(trader, tradee, 1);
        tradeManager.onPlayerEvent(trader, new PlayerMoveEvent(trader, Direction.DOWN, Coordinate.xy(1, 2)));
        verify(trader, times(0)).emitEvent(any(UpdateTradeWindowEvent.class));
        reset(trader);
        when(trader.coordinate()).thenReturn(Coordinate.xy(5, 2));
        tradeManager.onPlayerEvent(trader, new PlayerMoveEvent(trader, Direction.DOWN, Coordinate.xy(5, 3)));
        verify(trader, times(1)).emitEvent(any(UpdateTradeWindowEvent.class));
        verify(tradee, times(1)).emitEvent(any(UpdateTradeWindowEvent.class));
    }

    @Test
    void addItem() {
        var tradee = Mockito.mock(Player.class);
        when(tradee.tradeEnabled()).thenReturn(true);
        tradeManager.start(trader, tradee, 1);
    }
}
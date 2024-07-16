package org.y1000.entities.trade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;

import static org.junit.jupiter.api.Assertions.*;


class PlayerTradeTest {

    private PlayerTrade trade;
    private Player trader;
    private Player tradee;

    @BeforeEach
    void setUp() {
        trader = Mockito.mock(Player.class);
        tradee = Mockito.mock(Player.class);
        trade = new PlayerTrade(trader, tradee);
    }

    @Test
    void hasSpace() {
        assertTrue(trade.hasSpace(trader));
        assertTrue(trade.hasSpace(tradee));
        for (int i = 1; i <= 3; i++ ) {
            assertNotEquals(0, trade.addItem(trader,  Mockito.mock(Item.class)));
            assertNotEquals(0, trade.addItem(tradee,  Mockito.mock(Item.class)));
        }
        assertTrue(trade.hasSpace(trader));
        assertTrue(trade.hasSpace(tradee));
        assertNotEquals(0, trade.addItem(trader,  Mockito.mock(Item.class)));
        assertNotEquals(0, trade.addItem(tradee,  Mockito.mock(Item.class)));
        assertFalse(trade.hasSpace(trader));
        assertFalse(trade.hasSpace(tradee));
    }

    @Test
    void hasItem() {
        assertFalse(trade.hasItem(trader, 1));
        assertFalse(trade.hasItem(tradee, 1));
        assertFalse(trade.hasItem(tradee, 0));
        assertFalse(trade.hasItem(tradee, 5));
        trade.addItem(trader, Mockito.mock(Item.class));
        assertTrue(trade.hasItem(trader, 1));
        assertFalse(trade.hasItem(Mockito.mock(Player.class), 1));
    }

    @Test
    void removeItem() {
        trade.addItem(trader, Mockito.mock(Item.class));
        assertTrue(trade.removeItem(trader, 1).isPresent());
        assertFalse(trade.hasItem(trader, 1));
        assertTrue(trade.removeItem(trader, 1).isEmpty());
    }

    @Test
    void getItems() {
        assertTrue(trade.traderItems().isEmpty());
        assertTrue(trade.tradeeItems().isEmpty());
        trade.addItem(trader, Mockito.mock(Item.class));
        trade.addItem(trader, Mockito.mock(Item.class));
        trade.addItem(trader, Mockito.mock(Item.class));
        trade.removeItem(trader, 2);
        assertEquals(2, trade.traderItems().size());
    }
}
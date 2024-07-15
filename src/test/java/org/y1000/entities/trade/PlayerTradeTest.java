package org.y1000.entities.trade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.item.DefaultItem;

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
            trade.addItem(trader, i, DefaultItem.builder().name("test").desc("").dropSound("").eventSound("").build());
            trade.addItem(tradee, i, DefaultItem.builder().name("test").desc("").dropSound("").eventSound("").build());
        }
        assertTrue(trade.hasSpace(trader));
        assertTrue(trade.hasSpace(tradee));
        trade.addItem(trader, 4, DefaultItem.builder().name("test").desc("").dropSound("").eventSound("").build());
        trade.addItem(tradee, 4, DefaultItem.builder().name("test").desc("").dropSound("").eventSound("").build());
        assertFalse(trade.hasSpace(trader));
        assertFalse(trade.hasSpace(tradee));
    }
}
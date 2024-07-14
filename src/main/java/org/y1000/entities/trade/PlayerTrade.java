package org.y1000.entities.trade;


import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class PlayerTrade {
    private final Player trader;
    private final Player tradee;

    private final Map<Integer, Item> traderItems;
    private final Map<Integer, Item> tradeeItems;

    public PlayerTrade(Player trader, Player tradee) {
        this.trader = trader;
        this.tradee = tradee;
        traderItems = new HashMap<>();
        tradeeItems = new HashMap<>();
    }

}

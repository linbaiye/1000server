package org.y1000.entities.trade;


import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public final class PlayerTrade {
    private record TradingItem(int inventorySlot, Item item) {
    }

    private final Player trader;
    private final Player tradee;
    private boolean traderConfirmed;
    private boolean tradeeConfrimed;

    private final TradingItem[] traderItems;
    private final TradingItem[] tradeeItems;


    public PlayerTrade(Player trader, Player tradee) {
        this.trader = trader;
        this.tradee = tradee;
        traderItems = new TradingItem[4];
        tradeeItems = new TradingItem[4];
        tradeeConfrimed = false;
        traderConfirmed = false;
    }

    public void addItem(Player player, int inventorySlot, int tradeWindowSlot, Item item) {
        Validate.notNull(player);
        Validate.notNull(item);
        Validate.isTrue(tradeWindowSlot >= 1 && tradeWindowSlot <= 4);
        if (player.equals(trader)) {
            traderItems[tradeWindowSlot - 1] = new TradingItem(inventorySlot, item);
        } else if (player.equals(tradee)) {
            tradeeItems[tradeWindowSlot - 1] = new TradingItem(inventorySlot, item);
        }
    }

    public void onConfirmed(Player player) {
        Validate.notNull(player);
        if (player.equals(trader)) {
            traderConfirmed = !traderConfirmed;
        } else if (player.equals(tradee)) {
            tradeeConfrimed = !tradeeConfrimed;
        }
    }

    public boolean isBothConfirmed() {
        return traderConfirmed && tradeeConfrimed;
    }
}

package org.y1000.entities.trade;


import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

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


    private int findSpace(TradingItem[] slots)  {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasSpace(Player player) {
        if (trader.equals(player)) {
            return findSpace(traderItems) != -1;
        } else if (tradee.equals(player)) {
            return findSpace(tradeeItems) != -1;
        }
        return false;
    }

    public void addItem(Player player, int inventorySlot, Item item) {
        Validate.notNull(player);
        Validate.notNull(item);
        if (!hasSpace(player)) {
            return;
        }
        if (player.equals(trader)) {
            traderItems[findSpace(traderItems)] = new TradingItem(inventorySlot, item);
        } else if (player.equals(tradee)) {
            tradeeItems[findSpace(tradeeItems)] = new TradingItem(inventorySlot, item);
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

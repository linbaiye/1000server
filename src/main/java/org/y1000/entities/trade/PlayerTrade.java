package org.y1000.entities.trade;


import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;

import java.util.Optional;

@Getter
public final class PlayerTrade {

    private final Player trader;
    private final Player tradee;
    private boolean traderConfirmed;
    private boolean tradeeConfrimed;

    private final Item[] traderItems;
    private final Item[] tradeeItems;

    private static final int MAX_SIZE = 4;

    public PlayerTrade(Player trader, Player tradee) {
        this.trader = trader;
        this.tradee = tradee;
        traderItems = new Item[MAX_SIZE];
        tradeeItems = new Item[MAX_SIZE];
        tradeeConfrimed = false;
        traderConfirmed = false;
    }


    private int findEmptySlot(Item[] slots)  {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasSpace(Player player) {
        if (trader.equals(player)) {
            return findEmptySlot(traderItems) != -1;
        } else if (tradee.equals(player)) {
            return findEmptySlot(tradeeItems) != -1;
        }
        return false;
    }

    public int addItem(Player player, Item item) {
        Validate.notNull(player);
        Validate.notNull(item);
        if (!hasSpace(player)) {
            return 0;
        }
        if (player.equals(trader)) {
            int slot = findEmptySlot(traderItems);
            traderItems[slot] = item;
            return slot + 1;
        } else if (player.equals(tradee)) {
            int slot = findEmptySlot(tradeeItems);
            tradeeItems[slot] = item;
            return slot + 1;
        }
        return 0;
    }

    public boolean hasItem(Player player, int tradeWindowSlot) {
        if (player == null || tradeWindowSlot < 1 || tradeWindowSlot > MAX_SIZE) {
            return false;
        }
        if (trader.equals(player)) {
            return traderItems[tradeWindowSlot - 1] != null;
        } else if (tradee.equals(player)) {
            return tradeeItems[tradeWindowSlot - 1] != null;
        }
        return false;
    }

    public Optional<Item> removeItem(Player player, int tradeWindowSlot) {
        if (!hasItem(player, tradeWindowSlot)) {
            return Optional.empty();
        }
        Item item;
        var index = tradeWindowSlot - 1;
        if (trader.equals(player)) {
            item = traderItems[index];
            traderItems[index] = null;
        } else {
            item = tradeeItems[index];
            tradeeItems[index] = null;
        }
        return Optional.ofNullable(item);
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

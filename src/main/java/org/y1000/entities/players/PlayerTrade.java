package org.y1000.entities.players;

import org.y1000.item.Item;

public class PlayerTrade {
    private final Player player1;
    private final Player player2;

    public static class TradeItem {
        private final int slot;
        private Item item;
        public TradeItem(int slot, Item item) {
            this.slot = slot;
            this.item = item;
        }
    }

    private final TradeItem[] p1Items;
    private final TradeItem[] p2Items;

    public PlayerTrade(Player p1, Player p2) {
        p1Items = new TradeItem[4];
        p2Items = new TradeItem[4];
    }

    public boolean isTradingWith(Player player) {
        return another.equals(player);
    }
}

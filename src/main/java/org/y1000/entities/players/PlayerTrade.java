package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.event.CloseTradeWindowMessage;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.item.Item;
import org.y1000.message.ValueEnum;

import java.util.Optional;

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

    private boolean p1Confirmed;
    private boolean p2Confirmed;

    private final TradeItem[] p1Items;
    private final TradeItem[] p2Items;

    public PlayerTrade(Player p1, Player p2) {
        player1 = p1;
        player2 = p2;
        Validate.isTrue(!p1.equals(p2));
        p1Items = new TradeItem[4];
        p2Items = new TradeItem[4];
        p1Confirmed = false;
        p2Confirmed = false;
    }

    public Optional<Player> getAnother(Player player) {
        if (player1.equals(player))
            return Optional.of(player2);
        else if (player2.equals(player))
            return Optional.of(player1);
        return Optional.empty();
    }

    public void cancel(Player player) {
        if (player1.equals(player))
            player1.sendEvent(CloseTradeWindowMessage.of(player1));
        else if (player2.equals(player))
            player2.sendEvent(CloseTradeWindowMessage.of(player2));
    }

    public void confirm(Player player) {
        if (player1.equals(player))
            p1Confirmed = true;
        else if (player2.equals(player))
            p2Confirmed = true;
    }

    private void addToTrade(Player player, TradeItem[] tradeItems, int slot, long number) {
        Item item = player.inventory().getItem(slot);
        for (TradeItem tradeItem : tradeItems) {
            if (tradeItem.slot == slot) {
                if (!tradeItem.item.name().equals(item.name())) {
                    
                }
            }
        }
    }

    public void addTradeItem(Player player, int slot, int number) {
        if (!player.inventory().hasEnough(slot, number)) {
            player.sendEvent(PlayerTextMessage.of(player, "持有数量不足。"));
            return;
        }
        if (player1.equals(player)) {
            addToTrade(player, p1Items, slot, number);
        }
    }

}

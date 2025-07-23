package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.event.*;
import org.y1000.item.Item;
import org.y1000.item.StackItem;

import java.util.Optional;

public class PlayerTrade {
    private final Player player1;
    private final Player player2;

    public static class TradeItem {
        private final int fromInventorySlot;
        private Item item;
        public TradeItem(int slot, Item item) {
            this.fromInventorySlot = slot;
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

    private void giveItemsToPlayer(Player player, TradeItem[] tradeItems) {
        boolean rolled = false;
        for (TradeItem tradeItem : tradeItems) {
            if (tradeItem == null)
                continue;
            rolled = true;
            player.inventory().add(tradeItem.item);
        }
        if (rolled)
            player.sendEvent(UpdateInventoryMessage.quiet(player));
    }

    public void cancel(Player player) {
        if (!player1.equals(player) && !player2.equals(player))
            return;
        giveItemsToPlayer(player1, p1Items);
        giveItemsToPlayer(player2, p2Items);
        player1.sendEvent(CloseTradeWindowMessage.of(player1));
        player2.sendEvent(CloseTradeWindowMessage.of(player2));
        player1.closeTrade();
        player2.closeTrade();
    }

    public void confirm(Player player) {
        if (player1.equals(player))
            p1Confirmed = true;
        else if (player2.equals(player))
            p2Confirmed = true;
        if (p1Confirmed && p2Confirmed) {
            giveItemsToPlayer(player1, p2Items);
            giveItemsToPlayer(player2, p1Items);
            player1.sendEvent(CloseTradeWindowMessage.of(player1));
            player2.sendEvent(CloseTradeWindowMessage.of(player2));
            player1.closeTrade();
            player2.closeTrade();
        }
    }

    public void unconfirm(Player player) {
        if (player1.equals(player))
            p1Confirmed = false;
        else if (player2.equals(player))
            p2Confirmed = false;
    }

    private void syncTradeItem(Player player, TradeItem tradeItem, int tradeSlot) {
        player.sendEvent(UpdateTradeWindowSlotMessage.self(player, tradeSlot, tradeItem.item));
        if (player.equals(player1))
            player2.sendEvent(UpdateTradeWindowSlotMessage.another(player2, tradeSlot, tradeItem.item));
        else
            player1.sendEvent(UpdateTradeWindowSlotMessage.another(player1, tradeSlot, tradeItem.item));
    }


    private void removeFromInventory(Player player, int inventorySlot, long number) {
        Item item = player.inventory().getItem(inventorySlot);
        player.inventory().decrease(inventorySlot, number);
        item.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
        player.sendEvent(UpdateInventorySlotMessage.update(player, inventorySlot));
    }


    private void addItem(Player player, TradeItem[] tradeItems, int inventorySlot, long number) {
        Item item = player.inventory().getItem(inventorySlot);
        for (int i = 0; i < tradeItems.length; i++) {
            var tradeItem = tradeItems[i];
            if (tradeItem == null || tradeItem.fromInventorySlot != inventorySlot)
                continue;
            if (tradeItem.item.name().equals(item.name()) && tradeItem.item instanceof StackItem stackItem
                    && stackItem.hasMoreSpace(number)) {
                tradeItem.item = stackItem.increase(number);
                syncTradeItem(player, tradeItem, i + 1);
                removeFromInventory(player, inventorySlot, number);
                return;
            }
        }
        for (int i = 0; i < tradeItems.length; i++) {
            if (tradeItems[i] != null) {
                continue;
            }
            tradeItems[i] = new TradeItem(inventorySlot, item);
            syncTradeItem(player, tradeItems[i], i + 1);
            removeFromInventory(player, inventorySlot, number);
            return;
        }
        player.sendEvent(PlayerTextMessage.of(player, "交易窗口已满。"));
    }

    public void addTradeItem(Player player, int slot, int number) {
        if (!player.inventory().hasEnough(slot, number)) {
            player.sendEvent(PlayerTextMessage.of(player, "持有数量不足。"));
            return;
        }
        if (player1.equals(player)) {
            addItem(player, p1Items, slot, number);
        } else if (player2.equals(player))
            addItem(player, p2Items, slot, number);
    }

}

package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.OpenTradeWindowEvent;
import org.y1000.entities.players.event.UpdateTradeWindowEvent;
import org.y1000.entities.trade.PlayerTrade;
import org.y1000.event.EntityEvent;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class TradeManagerImpl implements TradeManager {
    private final Map<Player, PlayerTrade> ongoingTrades;


    public TradeManagerImpl() {
        ongoingTrades = new HashMap<>();
    }

    @Override
    public void start(Player trader, Player tradee, int slotId) {
        Validate.notNull(trader);
        Validate.notNull(tradee);
        Validate.notNull(trader.inventory().getItem(slotId));
        if (!tradee.tradeEnabled()) {
            trader.emitEvent(PlayerTextEvent.rejectTrade(trader));
            return;
        }
        if (trader.coordinate().directDistance(tradee.coordinate()) > 2) {
            trader.emitEvent(PlayerTextEvent.tooFarAway(trader));
            return;
        }
        if (ongoingTrades.containsKey(trader) || ongoingTrades.containsKey(tradee)) {
            trader.emitEvent(PlayerTextEvent.multiTrade(trader));
            return;
        }
        PlayerTrade trade = new PlayerTrade(trader, tradee);
        ongoingTrades.put(trader, trade);
        ongoingTrades.put(tradee, trade);
        trader.emitEvent(new OpenTradeWindowEvent(trader, tradee.id(), slotId));
        tradee.emitEvent(new OpenTradeWindowEvent(tradee, trader.id(), null));
    }

    private boolean needClose(EntityEvent event, Player trader, Player tradee) {
        if (event instanceof PlayerLeftEvent) {
            return true;
        } else if (event instanceof CreatureDieEvent) {
            return true;
        }
        return trader.coordinate().directDistance(tradee.coordinate()) > 2;
    }


    private void doClose(PlayerTrade trade) {
        trade.getTrader().emitEvent(UpdateTradeWindowEvent.close(trade.getTrader()));
        trade.getTradee().emitEvent(UpdateTradeWindowEvent.close(trade.getTradee()));
        ongoingTrades.remove(trade.getTradee());
        ongoingTrades.remove(trade.getTrader());
    }

    @Override
    public void onPlayerEvent(Player player, EntityEvent event) {
        if (player == null || event == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        PlayerTrade trade = ongoingTrades.get(player);
        if (needClose(event, trade.getTrader(), trade.getTradee())) {
            doClose(trade);
        }
    }

    @Override
    public void addTradeItem(Player player, int inventorySlot, long number) {
        if (player == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        Item item = player.inventory().getItem(inventorySlot);
        if (item == null) {
            return;
        }
        PlayerTrade trade = ongoingTrades.get(player);
        if (!trade.hasSpace(player) || !player.inventory().hasEnough(inventorySlot, number)) {
            return;
        }
        player.inventory().decrease(inventorySlot, number);
        player.emitEvent(new UpdateInventorySlotEvent(player, inventorySlot, player.inventory().getItem(inventorySlot)));
        if (item instanceof StackItem stackItem) {
            item = new StackItem(stackItem.item(), number);
        }
        int tradeWindowSlot = trade.addItem(player, item);
        player.emitEvent(UpdateTradeWindowEvent.add(player, tradeWindowSlot, item, true));
        var another = trade.getTrader().equals(player) ? trade.getTradee() : trade.getTrader();
        another.emitEvent(UpdateTradeWindowEvent.add(another, tradeWindowSlot, item, false));
    }


    private void putBackToInventory(Player player, Item item) {
        int slot = player.inventory().add(item);
        if (slot != 0) {
            player.emitEvent(new UpdateInventorySlotEvent(player, slot, player.inventory().getItem(slot)));
            return;
        }
        log.warn("Player {} lost item {}.", player.id(), item);
    }

    @Override
    public void removeTradeItem(Player player, int tradeWindowSlot) {
        if (player == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        PlayerTrade playerTrade = ongoingTrades.get(player);
        if (!playerTrade.hasItem(player, tradeWindowSlot)) {
            return;
        }
        playerTrade.removeItem(player, tradeWindowSlot).ifPresent(item -> putBackToInventory(player, item));
        Player trader = playerTrade.getTrader();
        Player tradee = playerTrade.getTradee();
        var active = trader.equals(player) ? trader : tradee;
        var passive = trader.equals(player) ? tradee: trader;
        active.emitEvent(UpdateTradeWindowEvent.remove(active, tradeWindowSlot, true));
        passive.emitEvent(UpdateTradeWindowEvent.remove(passive, tradeWindowSlot, false));
    }


    private void putItemsBack(Player player, Item[] items) {
        for (Item item : items) {
            if (item != null) {
                putBackToInventory(player, item);
            }
        }
    }

    @Override
    public void cancelTrade(Player player) {
        if (player == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        var trade = ongoingTrades.get(player);
        putItemsBack(trade.getTrader(), trade.getTraderItems());
        putItemsBack(trade.getTradee(), trade.getTradeeItems());
        doClose(trade);
    }
}

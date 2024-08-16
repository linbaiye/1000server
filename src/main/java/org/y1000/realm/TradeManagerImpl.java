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
import org.y1000.message.AbstractPositionEvent;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class TradeManagerImpl implements TradeManager {
    private final Map<Player, PlayerTrade> ongoingTrades;

    private final EntityEventSender eventSender;
    public TradeManagerImpl(EntityEventSender eventSender) {
        Validate.notNull(eventSender);
        ongoingTrades = new HashMap<>();
        this.eventSender = eventSender;
    }

    @Override
    public void start(Player trader, Player tradee, int slotId) {
        Validate.notNull(trader);
        Validate.notNull(tradee);
        if (trader.equals(tradee)) {
            return;
        }
        Validate.notNull(trader.inventory().getItem(slotId));
        if (!tradee.tradeEnabled()) {
            eventSender.notifySelf(PlayerTextEvent.rejectTrade(trader));
            return;
        }
        if (trader.coordinate().directDistance(tradee.coordinate()) > 2) {
            eventSender.notifySelf(PlayerTextEvent.tooFarAway(trader));
            return;
        }
        if (ongoingTrades.containsKey(trader) || ongoingTrades.containsKey(tradee)) {
            eventSender.notifySelf(PlayerTextEvent.multiTrade(trader));
            return;
        }
        PlayerTrade trade = new PlayerTrade(trader, tradee);
        ongoingTrades.put(trader, trade);
        ongoingTrades.put(tradee, trade);
        eventSender.notifySelf(new OpenTradeWindowEvent(trader, tradee.id(), slotId));
        eventSender.notifySelf(new OpenTradeWindowEvent(tradee, trader.id(), null));
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
        ongoingTrades.remove(trade.getTradee());
        ongoingTrades.remove(trade.getTrader());
        eventSender.notifySelf(UpdateTradeWindowEvent.close(trade.getTrader()));
        eventSender.notifySelf(UpdateTradeWindowEvent.close(trade.getTradee()));
    }

    @Override
    public void onPlayerEvent(Player player, EntityEvent event) {
        if (player == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        if (event instanceof PlayerLeftEvent || event instanceof InputResponseMessage ||
                event instanceof CreatureDieEvent) {
            PlayerTrade trade = ongoingTrades.get(player);
            if (needClose(event, trade.getTrader(), trade.getTradee())) {
                doCancel(player);
            }
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
        eventSender.notifySelf(new UpdateInventorySlotEvent(player, inventorySlot, player.inventory().getItem(inventorySlot)));
        if (item instanceof StackItem stackItem) {
            item = new StackItem(stackItem.item(), number);
        }
        int tradeWindowSlot = trade.addItem(player, item);
        eventSender.notifySelf(UpdateTradeWindowEvent.add(player, tradeWindowSlot, item, true));
        var another = trade.getTrader().equals(player) ? trade.getTradee() : trade.getTrader();
        eventSender.notifySelf(UpdateTradeWindowEvent.add(another, tradeWindowSlot, item, false));
    }


    private void addToInventory(Player player, Item item) {
        int slot = player.inventory().add(item);
        if (slot != 0) {
            eventSender.notifySelf(new UpdateInventorySlotEvent(player, slot, player.inventory().getItem(slot)));
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
        playerTrade.removeItem(player, tradeWindowSlot).ifPresent(item -> addToInventory(player, item));
        Player trader = playerTrade.getTrader();
        Player tradee = playerTrade.getTradee();
        var active = trader.equals(player) ? trader : tradee;
        var passive = trader.equals(player) ? tradee: trader;
        eventSender.notifySelf(UpdateTradeWindowEvent.remove(active, tradeWindowSlot, true));
        eventSender.notifySelf(UpdateTradeWindowEvent.remove(passive, tradeWindowSlot, false));
    }


    private void addItemsToInventory(Player player, List<Item> items) {
        items.forEach(i -> addToInventory(player, i));
    }


    private void doCancel(Player player) {
        var trade = ongoingTrades.get(player);
        addItemsToInventory(trade.getTrader(), trade.traderItems());
        addItemsToInventory(trade.getTradee(), trade.tradeeItems());
        doClose(trade);
    }

    @Override
    public void cancelTrade(Player player) {
        if (player == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        doCancel(player);
    }

    @Override
    public void confirmTrade(Player player) {
        if (player == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        var trade = ongoingTrades.get(player);
        trade.onConfirmed(player);
        if (!trade.isBothConfirmed()) {
            return;
        }
        if (!trade.getTrader().inventory().canTakeAll(trade.tradeeItems()) ||
                !trade.getTradee().inventory().canTakeAll(trade.traderItems())) {
            doCancel(player);
            eventSender.notifySelf(PlayerTextEvent.inventoryFull(trade.getTrader()));
            eventSender.notifySelf(PlayerTextEvent.inventoryFull(trade.getTradee()));
        } else {
            addItemsToInventory(trade.getTrader(), trade.tradeeItems());
            addItemsToInventory(trade.getTradee(), trade.traderItems());
            doClose(trade);
        }
    }
}

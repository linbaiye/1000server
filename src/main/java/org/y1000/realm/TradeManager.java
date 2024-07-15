package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.OpenTradeWindowEvent;
import org.y1000.entities.players.event.UpdateTradeWindowEvent;
import org.y1000.entities.trade.PlayerTrade;
import org.y1000.event.EntityEvent;
import org.y1000.item.AbstractStackItem;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;

import java.util.HashMap;
import java.util.Map;

public final class TradeManager {
    private final Map<Player, PlayerTrade> ongoingTrades;

    private final ItemFactory itemFactory;

    public TradeManager(ItemFactory itemFactory) {
        ongoingTrades = new HashMap<>();
        this.itemFactory = itemFactory;
    }

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

    public void onPlayerEvent(Player player, EntityEvent event) {
        if (player == null || event == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        PlayerTrade trade = ongoingTrades.get(player);
        if (!needClose(event, trade.getTrader(), trade.getTradee())) {
            return;
        }
        trade.getTrader().emitEvent(UpdateTradeWindowEvent.close(trade.getTrader()));
        trade.getTradee().emitEvent(UpdateTradeWindowEvent.close(trade.getTradee()));
        ongoingTrades.remove(trade.getTradee());
        ongoingTrades.remove(trade.getTrader());
    }

    public void addTradeItem(Player player, int inventorySlot, long number) {
        if (player == null || !ongoingTrades.containsKey(player)) {
            return;
        }
        Item item = player.inventory().getItem(inventorySlot);
        if (item == null) {
            return;
        }
        if (player.inventory().hasEnough(inventorySlot, number)) {
            player.inventory().decrease(inventorySlot, number);
        }
        if (item instanceof AbstractStackItem) {
            item = itemFactory.createItem(item.name(), number);
        }
        PlayerTrade trade = ongoingTrades.get(player);
        if (trade.hasSpace(player)) {
            return;
        }
        trade.addItem(player, inventorySlot, item);
    }
}

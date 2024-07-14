package org.y1000.realm;

import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.OpenTradeWindowEvent;
import org.y1000.entities.players.event.UpdateTradeWindowEvent;
import org.y1000.entities.trade.PlayerTrade;
import org.y1000.event.EntityEvent;
import org.y1000.message.PlayerMoveEvent;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;

import java.util.HashMap;
import java.util.Map;

public final class TradeManager {
    private final Map<Player, PlayerTrade> ongoingTrades;

    public TradeManager() {
        ongoingTrades = new HashMap<>();
    }

    public void start(Player trader, Player tradee, int slotId) {
        if (!tradee.tradeEnabled()) {
            trader.emitEvent(PlayerTextEvent.rejectTrade(trader));
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
        trader.emitEvent(new OpenTradeWindowEvent(tradee, trader.id(), null));
    }

    private boolean needClose(EntityEvent event, Player trader, Player tradee) {
        if (event instanceof PlayerLeftEvent) {
            return true;
        } else if (event instanceof CreatureDieEvent) {
            return true;
        } else if (event instanceof PlayerMoveEvent) {
            return trader.coordinate().directDistance(tradee.coordinate()) > 2;
        }
        return false;
    }

    public void onPlayerEvent(Player player, EntityEvent event) {
        if (!ongoingTrades.containsKey(player)) {
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
}

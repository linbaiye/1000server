package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;

public interface TradeManager {
    void start(Player trader, Player tradee, int slotId);

    void onPlayerEvent(Player player, EntityEvent event);

    void addTradeItem(Player player, int inventorySlot, long number);

    void removeTradeItem(Player player, int tradeWindowSlot);

    void cancelTrade(Player player);

    void confirmTrade(Player player);
}

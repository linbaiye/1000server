package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;
import org.y1000.trade.TradeItem;

import java.util.Collection;
import java.util.List;

public interface Merchant extends Npc {

    List<MerchantItem> buyItems();

    List<MerchantItem> sellItems();

    void buy(Player player, Collection<TradeItem> items);
}

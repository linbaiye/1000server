package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.trade.TradeItem;

import java.util.Collection;
import java.util.function.BiFunction;

public interface Merchant extends Npc {

    void buy(Player player, Collection<TradeItem> items);

    void sell(Player player, Collection<TradeItem> items, BiFunction<String, Long, Item> itemCreator);
}

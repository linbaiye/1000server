package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.trade.TradeItem;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Merchant extends Npc {

    void buy(Player player, Collection<TradeItem> items, Function<Long, StackItem> moneyCreator);

    void sell(Player player, Collection<TradeItem> items, BiFunction<String, Long, Item> itemCreator);
}

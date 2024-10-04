package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.trade.TradeItem;
import org.y1000.util.Coordinate;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Merchantable {
    void buy(Player player, Collection<TradeItem> items, Function<Long, StackItem> moneyCreator);

    void sell(Player player, Collection<TradeItem> items, BiFunction<String, Long, Item> itemCreator);

    default void sell(Player player, Collection<TradeItem> items, BiFunction<String, Long, Item> itemCreator, Coordinate coordinate) {
        if (player == null || !player.canBeSeenAt(coordinate)) {
            return;
        }
        sell(player, items, itemCreator);
    }

    default void buy(Player player, Collection<TradeItem> items, Function<Long, StackItem> moneyCreator, Coordinate coordinate) {
        if (player == null || !player.canBeSeenAt(coordinate)) {
            return;
        }
        buy(player, items, moneyCreator);
    }
}

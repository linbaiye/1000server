package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.trade.TradeItem;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

final class MerchantableImpl implements Merchantable {
    private final List<MerchantItem> buyItems;

    private final List<MerchantItem> sellItems;


    public MerchantableImpl(List<MerchantItem> buyItems, List<MerchantItem> sellItems) {
        this.buyItems = buyItems != null ? buyItems : Collections.emptyList();
        this.sellItems = sellItems != null ? sellItems : Collections.emptyList();
    }

    private boolean containsAll(Collection<TradeItem> items, List<MerchantItem> merchantItems) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (TradeItem tradeItem: items) {
            boolean found = merchantItems.stream().anyMatch(i -> i.name().equals(tradeItem.name()));
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private boolean canBuy(Collection<TradeItem> items) {
        return containsAll(items, buyItems);
    }


    private boolean canSell(Collection<TradeItem> items) {
        return containsAll(items, sellItems);
    }

    private long computeTotal(Collection<TradeItem> items, Collection<MerchantItem> merchantItems) {
        long total = 0;
        for (TradeItem item: items) {
            total += merchantItems.stream().filter(mi -> mi.name().equals(item.name()))
                    .findFirst()
                    .map(mi -> mi.price() * item.number())
                    .orElse(0);
        }
        return total;
    }

    private long computePlayerCost(Collection<TradeItem> items) {
        return computeTotal(items, sellItems);
    }

    private long computePlayerProfit(Collection<TradeItem> items) {
        return computeTotal(items, buyItems);
    }

    @Override
    public void buy(Player player, Collection<TradeItem> items, Function<Long, StackItem> moneyCreator) {
        if (player == null || items == null || moneyCreator == null) {
            return;
        }
        if (player.inventory().canSell(items) && canBuy(items)) {
            player.inventory().sell(items, moneyCreator.apply(computePlayerProfit(items)), player);
        }
    }

    @Override
    public void sell(Player player, Collection<TradeItem> items, BiFunction<String, Long, Item> itemCreator) {
        if (player == null || items == null || itemCreator == null) {
            return;
        }
        var cost = computePlayerCost(items);
        if (player.inventory().canBuy(items, cost) && canSell(items)) {
            player.inventory().buy(items, cost, player, itemCreator);
        }
    }

    @Override
    public void appendAbilityNames(List<String> names) {
        if (names == null)
            return;
        if (!sellItems.isEmpty())
            names.add("买物品");
        if (!buyItems.isEmpty())
            names.add("卖物品");
    }
}

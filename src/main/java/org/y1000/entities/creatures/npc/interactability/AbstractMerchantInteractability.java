package org.y1000.entities.creatures.npc.interactability;

import org.y1000.entities.creatures.npc.MerchantItem;
import org.y1000.trade.TradeItem;

import java.util.Collection;
import java.util.List;

public abstract class AbstractMerchantInteractability implements NpcInteractability {

    protected boolean containsAll(Collection<TradeItem> items, List<MerchantItem> merchantItems) {
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

    protected long computeTotal(Collection<TradeItem> items, Collection<MerchantItem> merchantItems) {
        long total = 0;
        for (TradeItem item: items) {
            total += merchantItems.stream().filter(mi -> mi.name().equals(item.name()))
                    .findFirst()
                    .map(mi -> mi.price() * item.number())
                    .orElse(0);
        }
        return total;
    }
}

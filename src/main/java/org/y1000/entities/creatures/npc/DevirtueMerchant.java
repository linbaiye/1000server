package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.trade.TradeItem;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public final class DevirtueMerchant extends AbstractNpc implements Merchant {

    private final SubmissiveWanderingAI ai;

    private final List<MerchantItem> buyItems;
    private final List<MerchantItem> sellItems;
    private final String textFileName;

    @Builder
    public DevirtueMerchant(long id, Coordinate coordinate, Direction direction,
                            String name,
                            Map<State, Integer> stateMillis,
                            SubmissiveWanderingAI ai,
                            AttributeProvider attributeProvider,
                            RealmMap realmMap,
                            List<MerchantItem> buy,
                            List<MerchantItem> sell,
                            String textFileName) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, null);
        Validate.notNull(sell);
        Validate.notNull(buy);
        Validate.notNull(textFileName);
        Validate.notNull(ai);
        this.textFileName = textFileName;
        this.ai = ai;
        this.buyItems = buy;
        this.sellItems = sell;
    }


    @Override
    public void update(int delta) {
        state().update(this, delta);
    }


    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(),
                NpcType.MERCHANT, attributeProvider().animate(), attributeProvider().shape(), textFileName);
    }

    @Override
    public void onActionDone() {
        handleActionDone(() -> ai.onActionDone(this));
    }

    @Override
    public void onMoveFailed() {
        ai.onMoveFailed(this);
    }


    @Override
    public void respawn(Coordinate coordinate) {
        doRevive(coordinate);
        ai.start(this);
    }

    @Override
    public void start() {
        ai.start(this);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return obj == this || ((DevirtueMerchant) obj).id() == id();
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
        if (player == null || items == null || !player.canBeSeenAt(coordinate())) {
            return;
        }
        if (player.inventory().canSell(items) && canBuy(items)) {
            player.inventory().sell(items, moneyCreator.apply(computePlayerProfit(items)), player);
        }
    }

    @Override
    public void sell(Player player, Collection<TradeItem> items, BiFunction<String, Long, Item> itemCreator) {
        if (player == null || items == null || !player.canBeSeenAt(coordinate())) {
            return;
        }
        var cost = computePlayerCost(items);
        if (player.inventory().canBuy(items, cost) && canSell(items)) {
            player.inventory().buy(items, cost, player, itemCreator);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}

package org.y1000.entities.creatures.npc;

import lombok.Builder;
import org.y1000.entities.Direction;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.trade.TradeItem;
import org.y1000.util.Coordinate;

import java.util.*;

public final class DevirtueMerchant extends AbstractNpc implements Merchant {

    private final DevirtueMerchantAI ai;

    private final List<MerchantItem> buyItems;
    private final List<MerchantItem> sellItems;


    @Builder
    public DevirtueMerchant(long id, Coordinate coordinate, Direction direction,
                            String name,
                            Map<State, Integer> stateMillis,
                            DevirtueMerchantAI ai,
                            AttributeProvider attributeProvider,
                            RealmMap realmMap,
                            List<MerchantItem> buy,
                            List<MerchantItem> sell) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap);
        Objects.requireNonNull(ai);
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
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), name(), NpcType.MERCHANT);
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
    public void revive(Coordinate coordinate) {
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

    @Override
    public List<MerchantItem> buyItems() {
        return buyItems;
    }

    @Override
    public List<MerchantItem> sellItems() {
        return sellItems;
    }

    private Optional<MerchantItem> find(String name) {
        return buyItems().stream().filter(i -> i.name().equals(name)).findFirst();
    }

    private boolean buy(String name) {
        return buyItems().stream().anyMatch(i -> i.name().equals(name));
    }

    public boolean canBuy(Collection<TradeItem> items) {
        if (items == null) {
            return false;
        }
        for (TradeItem buyingItems: items) {
            if (!buy(buyingItems.name())) {
                return false;
            }
        }
        return true;
    }

    private long computeProfit(Collection<TradeItem> items) {
        long total = 0;
        for (TradeItem item: items) {
            total += find(item.name())
                    .map(mi -> mi.price() * item.number())
                    .orElse(0);
        }
        return total;
    }


    @Override
    public void buy(Player player, Collection<TradeItem> items) {
        if (!player.canBeSeenAt(coordinate())) {
            return;
        }
        if (player.inventory().canSell(items) && canBuy(items)) {
            player.inventory().sell(items, computeProfit(items), player);
        }
    }
}

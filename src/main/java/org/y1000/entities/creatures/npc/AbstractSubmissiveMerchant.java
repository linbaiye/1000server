package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.npc.AI.SubmissiveWanderingAI;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.realm.RealmMap;
import org.y1000.trade.TradeItem;
import org.y1000.util.Coordinate;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractSubmissiveMerchant extends AbstractNpc implements Merchant {

    private final Merchantable merchantable;

    private final String merchantFile;

    public AbstractSubmissiveMerchant(long id,
                                      Coordinate coordinate,
                                      String name,
                                      Map<State, Integer> stateMillis,
                                      AttributeProvider attributeProvider,
                                      RealmMap realmMap,
                                      Merchantable merchantable,
                                      String merchantFile) {
        super(id, coordinate, Direction.DOWN, name, stateMillis, attributeProvider, realmMap, null, new SubmissiveWanderingAI());
        Validate.notNull(merchantable);
        Validate.notNull(merchantFile);
        this.merchantable = merchantable;
        this.merchantFile = merchantFile;
    }

    protected String getMerchantFile() {
        return merchantFile;
    }

    @Override
    public void update(int delta) {
        state().update(this, delta);
    }

    @Override
    public void buy(Player player, Collection<TradeItem> items, Function<Long, StackItem> moneyCreator) {
        merchantable.buy(player, items, moneyCreator, coordinate());
    }


    @Override
    public void startIdleAI() {
        getAI().start(this);
    }

    @Override
    public void sell(Player player, Collection<TradeItem> items, BiFunction<String, Long, Item> itemCreator) {
        merchantable.sell(player, items, itemCreator, coordinate());
    }

    @Override
    void hurt(ViolentCreature attacker) {
        doHurtAction(attacker, getStateMillis(State.HURT));
    }
}

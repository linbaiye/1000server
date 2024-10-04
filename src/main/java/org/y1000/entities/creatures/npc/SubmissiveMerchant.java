package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.npc.AI.SubmissiveWanderingAI;
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
public final class SubmissiveMerchant extends AbstractNpc implements Merchant {

    private final Merchantable merchantable;

    private final String merchantFileName;

    @Builder
    public SubmissiveMerchant(long id, Coordinate coordinate, Direction direction,
                              String name,
                              Map<State, Integer> stateMillis,
                              SubmissiveWanderingAI ai,
                              AttributeProvider attributeProvider,
                              Merchantable merchantable,
                              String fileName,
                              RealmMap realmMap) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, null, ai);
        Validate.notNull(merchantable);
        Validate.notNull(fileName);
        this.merchantable = merchantable;
        this.merchantFileName = fileName;
    }


    @Override
    public void update(int delta) {
        state().update(this, delta);
    }


    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(),
                NpcType.MERCHANT, attributeProvider().animate(), attributeProvider().shape(), merchantFileName);
    }

    @Override
    public void changeToIdleAI() {

    }

    @Override
    public void startIdleAI() {
        getAI().start(this);
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
        return obj == this || ((SubmissiveMerchant) obj).id() == id();
    }

    @Override
    public void buy(Player player, Collection<TradeItem> items, Function<Long, StackItem> moneyCreator) {
        merchantable.buy(player, items, moneyCreator, coordinate());
    }

    @Override
    public void sell(Player player, Collection<TradeItem> items, BiFunction<String, Long, Item> itemCreator) {
        merchantable.sell(player, items, itemCreator, coordinate());
    }

    @Override
    void hurt(ViolentCreature attacker) {
        doHurtAction(attacker, getStateMillis(State.HURT));
    }

    @Override
    protected Logger log() {
        return log;
    }
}

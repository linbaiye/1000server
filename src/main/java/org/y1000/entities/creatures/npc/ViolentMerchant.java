package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.AI.ViolentNpcWanderingAI;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.trade.TradeItem;
import org.y1000.util.Coordinate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public final class ViolentMerchant extends AbstractViolentNpc implements Merchant {
    private final Merchantable merchantable;
    private final String fileName;

    @Builder
    public ViolentMerchant(long id, Coordinate coordinate,
                           Direction direction, String name,
                           Map<State, Integer> stateMillis,
                           AttributeProvider attributeProvider,
                           RealmMap realmMap,
                           NpcAI ai,
                           NpcRangedSkill skill,
                           List<NpcSpell> spellList,
                           Merchantable merchantable,
                           String fileName) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, ai, skill, spellList);
        this.merchantable = merchantable;
        this.fileName = fileName;
    }

    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(),
                NpcType.MERCHANT, attributeProvider().animate(), attributeProvider().shape(), fileName);
    }

    @Override
    public void changeToIdleAI() {
        changeAI(new ViolentNpcWanderingAI(spawnCoordinate()));
    }

    @Override
    public void startIdleAI() {
        changeAndStartAI(new ViolentNpcWanderingAI(spawnCoordinate()));
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
        return obj == this || ((ViolentMerchant) obj).id() == id();
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
    protected Logger log() {
        return log;
    }
}

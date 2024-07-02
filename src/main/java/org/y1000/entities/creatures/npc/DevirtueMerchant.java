package org.y1000.entities.creatures.npc;

import lombok.Builder;
import org.y1000.entities.Direction;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DevirtueMerchant extends AbstractNpc implements Merchant {

    private final DevirtueMerchantAI ai;

    @Builder
    public DevirtueMerchant(long id, Coordinate coordinate, Direction direction,
                            String name,
                            Map<State, Integer> stateMillis,
                            DevirtueMerchantAI ai,
                            AttributeProvider attributeProvider,
                            RealmMap realmMap) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap);
        Objects.requireNonNull(ai);
        this.ai = ai;
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
        return Collections.emptyList();
    }

    @Override
    public List<MerchantItem> sellItems() {
        return Collections.emptyList();
    }
}

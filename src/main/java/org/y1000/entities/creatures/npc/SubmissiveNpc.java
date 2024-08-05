package org.y1000.entities.creatures.npc;

import lombok.Builder;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SubmissiveNpc extends AbstractNpc {

    private final NpcAI ai;

    @Builder
    public SubmissiveNpc(long id, Coordinate coordinate, Direction direction, String name, Map<State, Integer> stateMillis,
                         AttributeProvider attributeProvider,
                         RealmMap realmMap, NpcAI ai, List<NpcSpell> spells) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, spells);
        Validate.notNull(ai);
        this.ai = ai;
    }

    @Override
    public void update(int delta) {
        state().update(this, delta);
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(), NpcType.MONSTER,
                attributeProvider().animate(), attributeProvider().shape());
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
    public NpcAI AI() {
        return ai;
    }

    @Override
    public void respawn(Coordinate coordinate) {
        doRevive(coordinate);
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
        return obj == this || ((SubmissiveNpc) obj).id() == id();
    }

    @Override
    public void start() {
        ai.start(this);
    }
}

package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;

public abstract class AbstractSubmissiveNpc extends AbstractNpc {

    protected final NpcAI ai;

    public AbstractSubmissiveNpc(long id, Coordinate coordinate, Direction direction, String name, Map<State, Integer> stateMillis,
                                 AttributeProvider attributeProvider, RealmMap realmMap,
                                 List<NpcSpell> spells, NpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, spells);
        Validate.notNull(ai);
        this.ai = ai;
    }

    abstract NpcType getType();

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(), getType(),
                attributeProvider().animate(), attributeProvider().shape());
    }
    @Override
    public void update(int delta) {
        state().update(this, delta);
    }

    @Override
    void hurt(ViolentCreature attacker) {
        doHurtAction(attacker, getStateMillis(State.HURT));
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
    }

    @Override
    public void start() {
        ai.start(this);
    }
}

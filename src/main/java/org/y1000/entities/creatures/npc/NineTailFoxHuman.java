package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.event.NpcShiftEvent;
import org.y1000.entities.creatures.npc.AI.SubmissiveWanderingAI;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.Map;

@Slf4j
public final class NineTailFoxHuman extends AbstractNpc {

    @Builder
    public NineTailFoxHuman(long id, Coordinate coordinate, Direction direction, String name,
                            Map<State, Integer> stateMillis, AttributeProvider attributeProvider,
                            RealmMap realmMap, SubmissiveWanderingAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, Collections.emptyList(), ai);
    }

    @Override
    public void update(int delta) {
        state().update(this, delta);
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(),
                NpcType.MONSTER, attributeProvider().animate(), attributeProvider().shape());
    }

    @Override
    void hurt(ViolentCreature attacker) {
        doHurtAction(attacker, getStateMillis(State.HURT));
    }

    @Override
    protected Logger log() {
        return log;
    }


    public void shift() {
        emitEvent(new NpcShiftEvent("九尾狐变身", this));
    }



    @Override
    public void startIdleAI() {
        getAI().start(this);
    }

}

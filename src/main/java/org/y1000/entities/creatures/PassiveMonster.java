package org.y1000.entities.creatures;


import org.y1000.entities.Direction;
import org.y1000.entities.players.State;
import org.y1000.message.AbstractInterpolation;
import org.y1000.message.CreatureInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Objects;


public final class PassiveMonster extends AbstractCreature {

    private CreatureState<PassiveMonster> state;

    private final PassiveMonsterAI creatureAI;

    private final RealmMap realmMap;

    public PassiveMonster(long id, Coordinate coordinate, Direction direction, String name,
                          RealmMap realmMap) {
        super(id, coordinate, direction, name);
        state = PassiveMonsterIdleState.buffalo();
        creatureAI = new PassiveMonsterAI(this);
        this.realmMap = realmMap;
    }

    void changeState(CreatureState<PassiveMonster> newState) {
        state = newState;
    }

    PassiveMonsterAI AI() {
        return creatureAI;
    }

    RealmMap realmMap() {
        return realmMap;
    }

    CreatureState<PassiveMonster> state() {
        return state;
    }

    @Override
    public void update(int delta) {
        state.update(this, delta);
    }

    @Override
    public AbstractInterpolation captureInterpolation() {
        return new CreatureInterpolation(id(), coordinate(), state.stateEnum(), direction(), state.elapsedMillis(), name());
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
        return obj == this || ((PassiveMonster) obj).id() == id();
    }

    @Override
    public State stateEnum() {
        return state.stateEnum();
    }
}

package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AggressiveMonster;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Objects;

@Slf4j
public final class Guardian extends AbstractViolentNpc {

    @Getter
    private final int width;

    @Builder
    public Guardian(long id, Coordinate coordinate, Direction direction, String name, Map<State, Integer> stateMillis, AttributeProvider attributeProvider, RealmMap realmMap, NpcAI ai, int width) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, ai, null, null);
        this.width = width;
    }


    @Override
    public void update(int delta) {
        doUpdate(delta);
    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(),
                NpcType.GUARDIAN, attributeProvider().animate(), attributeProvider().shape());
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void respawn(Coordinate coordinate) {
        doRevive(coordinate);
    }

    public void attackMonster(AggressiveMonster monster) {
        if (monster == null) {
            return;
        }
        if (getAi() instanceof GuardWanderingAI) {
            changeAI(new ViolentNpcMeleeFightAI(monster, this));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return obj == this || ((Guardian) obj).id() == id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    @Override
    public void changeToIdleAI() {
        changeAI(new GuardWanderingAI());
    }
}

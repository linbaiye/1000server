package org.y1000.entities.creatures.monster;


import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.NpcAI;
import org.y1000.entities.creatures.npc.NpcRangedSkill;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Objects;


@Slf4j
public final class PassiveMonster extends AbstractMonster {

    @Builder
    public PassiveMonster(long id, Coordinate coordinate, Direction direction, String name,
                          RealmMap realmMap, Map<State, Integer> stateMillis,
                          AttributeProvider attributeProvider,
                          NpcRangedSkill skill, NpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, ai, skill);
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
    protected Logger log() {
        return log;
    }

    @Override
    public void update(int delta) {
        cooldown(delta);
        state().update(this, delta);
        skill().ifPresent(s -> s.cooldown(delta));
    }
}

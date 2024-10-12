package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public final class SubmissiveNpc extends AbstractSubmissiveNpc {

    @Builder
    public SubmissiveNpc(long id, Coordinate coordinate, String name, Map<State, Integer> stateMillis,
                         AttributeProvider attributeProvider,
                         RealmMap realmMap, NpcAI ai, List<NpcSpell> spells) {
        super(id, coordinate, Direction.DOWN, name, stateMillis, attributeProvider, realmMap, spells, ai);
    }

    @Override
    protected Logger log() {
        return log;
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
    protected NpcType getType() {
        return NpcType.MONSTER;
    }

}

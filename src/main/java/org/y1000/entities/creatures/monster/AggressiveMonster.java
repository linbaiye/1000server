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

@Slf4j
public final class AggressiveMonster extends AbstractMonster {
    @Builder
    public AggressiveMonster(long id, Coordinate coordinate, Direction direction,
                             String name, Map<State, Integer> stateMillis,
                             AttributeProvider attributeProvider,
                             RealmMap realmMap, NpcRangedSkill skill, NpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider,
                realmMap, ai, skill);
    }
    @Override
    protected Logger log() {
        return log;
    }


}

package org.y1000.entities.creatures.monster;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;

@Slf4j
public final class AggressiveMonster extends AbstractMonster {
    @Builder
    public AggressiveMonster(long id, Coordinate coordinate, Direction direction, String name,
                          RealmMap realmMap, Map<State, Integer> stateMillis,
                          AttributeProvider attributeProvider, MonsterAttackSkill spell) {
        super(id, coordinate, direction, name, realmMap, stateMillis, attributeProvider, spell);
    }

    public void onCreatureAppear(Creature creature) {
        if (getFightingEntity() == null) {
            setFightingEntity(creature);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}

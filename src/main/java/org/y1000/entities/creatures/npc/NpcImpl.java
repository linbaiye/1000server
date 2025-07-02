package org.y1000.entities.creatures.npc;

import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.monster.NpcStateEnum;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;

public class NpcImpl extends AbstractNpc {
    public NpcImpl(long id, Coordinate coordinate, Direction direction, String name, Map<NpcStateEnum, Integer> stateMillis,
                   AttributeProvider attributeProvider, RealmMap realmMap, List<NpcSpell> spells, NpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, spells, ai);
    }

    @Override
    public void update(int delta) {

    }

    @Override
    void hurt(ViolentCreature attacker) {

    }

    @Override
    protected Logger log() {
        return null;
    }

    @Override
    public void startIdleAI() {

    }
}

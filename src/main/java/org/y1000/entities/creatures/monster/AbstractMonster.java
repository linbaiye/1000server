package org.y1000.entities.creatures.monster;


import org.y1000.entities.Direction;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.AbstractViolentNpc;
import org.y1000.entities.creatures.npc.NpcAI;
import org.y1000.entities.creatures.npc.ViolentNpcWanderingAI;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;

public abstract class AbstractMonster extends AbstractViolentNpc implements Monster {
    public AbstractMonster(long id, Coordinate coordinate, Direction direction, String name, Map<State, Integer> stateMillis, AttributeProvider attributeProvider, RealmMap realmMap, NpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, ai);
    }

    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), name(), NpcType.MONSTER);
    }

    @Override
    public void changeAI(NpcAI newAI) {
        if (newAI instanceof ViolentNpcWanderingAI wanderingAI) {
            super.changeAI(new MonsterWanderingAI(wanderingAI));
        } else {
            super.changeAI(newAI);
        }
    }


    @Override
    public void revive(Coordinate coordinate) {
        doRevive(coordinate);
        super.changeAI(new MonsterWanderingAI(new ViolentNpcWanderingAI()));
    }
}

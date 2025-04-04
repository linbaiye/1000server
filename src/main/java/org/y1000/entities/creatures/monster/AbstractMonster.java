package org.y1000.entities.creatures.monster;


import org.y1000.entities.Direction;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.AI.MonsterWanderingAI;
import org.y1000.entities.creatures.npc.AbstractViolentNpc;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.NpcRangedSkill;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.NpcInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractMonster extends AbstractViolentNpc implements Monster {

    private final NpcAI initAi;
    public AbstractMonster(long id, Coordinate coordinate, Direction direction, String name, Map<State, Integer> stateMillis,
                           AttributeProvider attributeProvider,
                           RealmMap realmMap,
                           NpcAI ai, NpcRangedSkill sk, List<NpcSpell> spellList) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, ai, sk, spellList);
        this.initAi = ai;
    }

    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
        return new NpcInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), viewName(), NpcType.MONSTER,
                attributeProvider().animate(), attributeProvider().shape());
    }


    @Override
    public Optional<String> normalSound() {
        return attributeProvider().normalSound();
    }


    @Override
    public void startIdleAI() {
        changeAndStartAI(new MonsterWanderingAI(spawnCoordinate()));
    }

    @Override
    public int escapeLife() {
        return attributeProvider().escapeLife();
    }
}

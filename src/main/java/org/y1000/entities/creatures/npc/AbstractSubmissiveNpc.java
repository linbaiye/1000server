package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.IActiveEntity;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.entities.creatures.npc.AI.INpcAI;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;

public abstract class AbstractSubmissiveNpc extends AbstractNpc {

    public AbstractSubmissiveNpc(long id, Coordinate coordinate, Direction direction, String name, Map<NpcAnimationEnum, Integer> stateMillis,
                                 AttributeProvider attributeProvider, RealmMap realmMap,
                                 List<NpcSpell> spells, INpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, spells, ai);
        Validate.notNull(ai);
    }

    protected abstract NpcType getType();


    @Override
    public void update(int delta) {
        npcState().update(delta);
    }

    @Override
    void hurt(IActiveEntity attacker) {
        doHurtAction(attacker, getStateMillis(NpcAnimationEnum.Hurt));
    }

    @Override
    public void startIdleAI() {
        getAI().start(this);
    }
}

package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.npc.AI.NpcAI;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.message.AbstractEntitySnapshot;
import org.y1000.message.NpcSnapshot;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;

public abstract class AbstractSubmissiveNpc extends AbstractNpc {

    public AbstractSubmissiveNpc(long id, Coordinate coordinate, Direction direction, String name, Map<PlayerStateEnum, Integer> stateMillis,
                                 AttributeProvider attributeProvider, RealmMap realmMap,
                                 List<NpcSpell> spells, NpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, spells, ai);
        Validate.notNull(ai);
    }

    protected abstract NpcType getType();

    @Override
    public AbstractEntitySnapshot captureInterpolation() {
        return new NpcSnapshot(id(), coordinate(), creatureState().stateEnum(), direction(), creatureState().elapsedMillis(), viewName(), getType(),
                attributeProvider().animate(), attributeProvider().shape());
    }
    @Override
    public void update(int delta) {
        creatureState().update(this, delta);
    }

    @Override
    void hurt(ViolentCreature attacker) {
        doHurtAction(attacker, getStateMillis(PlayerStateEnum.HURT));
    }


    @Override
    public void startIdleAI() {
        getAI().start(this);
    }
}

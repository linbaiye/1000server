package org.y1000.entities.creatures.monster;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.creatures.npc.AI.INpcAI;
import org.y1000.entities.creatures.npc.AI.ViolentNpcMeleeFightAI;
import org.y1000.entities.creatures.npc.AggressiveNpc;
import org.y1000.entities.creatures.npc.NpcRangedSkill;
import org.y1000.entities.creatures.npc.spell.NpcSpell;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Map;

@Slf4j
public final class AggressiveMonster extends AbstractMonster implements AggressiveNpc  {

    @Builder
    public AggressiveMonster(long id, Coordinate coordinate, Direction direction,
                             String name, Map<NpcActionEnum, Integer> stateMillis,
                             AttributeProvider attributeProvider,
                             RealmMap realmMap, NpcRangedSkill skill, INpcAI ai, List<NpcSpell> spells) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider,
                realmMap, ai, skill, spells);
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void actAggressively(AttackableEntity enemy) {
        if (canChaseOrAttack(enemy))
            changeAndStartAI(new ViolentNpcMeleeFightAI(enemy, this));
    }

}

package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;

@Slf4j
public final class Scarecrow extends AbstractSubmissiveNpc {

    @Builder
    public Scarecrow(long id,
                     Coordinate coordinate,
                     String name,
                     Map<State, Integer> stateMillis,
                     AttributeProvider attributeProvider,
                     RealmMap realmMap) {
        super(id, coordinate, Direction.DOWN, name, stateMillis, attributeProvider, realmMap, null, NpcFrozenAI.INSTANCE);
    }

    @Override
    protected Logger log() {
        return log;
    }


    @Override
    public boolean attackedBy(Player attacker) {
        boolean canGainExp = attacker.attackKungFu().level() < 7500;
        return doAttacked(attacker.damage(), attacker.hit(), amount -> {
            if (canGainExp)
                attacker.gainAttackExp(amount);
        }, attacker);
    }

    @Override
    protected NpcType getType() {
        return NpcType.MONSTER;
    }
}

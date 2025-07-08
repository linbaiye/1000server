package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.NpcType;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.entities.creatures.npc.AI.INpcAI;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;

@Slf4j
public final class Banker extends AbstractSubmissiveNpc {

    @Builder
    public Banker(long id, Coordinate coordinate,
                  Direction direction, String name, Map<NpcAnimationEnum, Integer> stateMillis,
                  AttributeProvider attributeProvider,
                  RealmMap realmMap, INpcAI ai) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, null, ai);
    }

    public boolean allowOperation(Player player) {
        return player != null && canBeSeenAt(player.coordinate());
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected NpcType getType() {
        return NpcType.BANKER;
    }

}

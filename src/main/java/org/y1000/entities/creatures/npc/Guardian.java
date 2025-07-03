package org.y1000.entities.creatures.npc;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttributeProvider;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.AI.GuardWanderingAI;
import org.y1000.entities.creatures.npc.AI.INpcAI;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Objects;

@Slf4j
public final class Guardian extends AbstractViolentNpc implements HumanNpc {

    @Getter
    private final int width;

    @Builder
    public Guardian(long id, Coordinate coordinate, Direction direction, String name, Map<NpcActionEnum, Integer> stateMillis, AttributeProvider attributeProvider, RealmMap realmMap, INpcAI ai, int width) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap, ai, null, null);
        this.width = width;
    }



    @Override
    protected Logger log() {
        return log;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return obj == this || ((Guardian) obj).id() == id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }


    @Override
    public void startIdleAI() {
        changeAndStartAI(new GuardWanderingAI(this.wanderingArea().random(coordinate())));
    }
}

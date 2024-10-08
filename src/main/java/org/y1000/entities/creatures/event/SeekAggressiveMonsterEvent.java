package org.y1000.entities.creatures.event;

import lombok.Getter;
import org.hibernate.engine.internal.AbstractEntityEntry;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AggressiveMonster;
import org.y1000.entities.creatures.npc.Guardian;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.event.EntityEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class SeekAggressiveMonsterEvent implements EntityEvent {

    private final Guardian guardian;
    private final int width;
    @Getter
    private final Set<AggressiveMonster> monsters;
    public SeekAggressiveMonsterEvent(Guardian guardian, int width) {
        this.guardian = guardian;
        this.width = width;
        monsters = new HashSet<>();
    }

    public Entity source() {
        return guardian;
    }

    public void handle(Stream<Npc> entityStream) {
        entityStream.filter(npc -> npc instanceof AggressiveMonster &&
                        npc.stateEnum() != State.DIE &&
                        npc.coordinate().directDistance(guardian.coordinate()) <= width)
                .map(AggressiveMonster.class::cast)
                .forEach(monsters::add);
    }
}

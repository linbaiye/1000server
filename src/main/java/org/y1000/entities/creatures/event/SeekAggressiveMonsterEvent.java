package org.y1000.entities.creatures.event;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AggressiveMonster;
import org.y1000.entities.creatures.npc.Guardian;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;

import java.util.stream.Stream;

public record SeekAggressiveMonsterEvent(Guardian guardian, int width) implements EntityEvent {

    public Entity source() {
        return guardian;
    }

    public void handle(Stream<Npc> entityStream) {
        entityStream.filter(npc -> npc instanceof AggressiveMonster &&
                        npc.stateEnum() != State.DIE &&
                        npc.coordinate().directDistance(guardian.coordinate()) <= width)
                .map(AggressiveMonster.class::cast)
                .findFirst()
                .ifPresent(guardian::attackMonster);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }
}

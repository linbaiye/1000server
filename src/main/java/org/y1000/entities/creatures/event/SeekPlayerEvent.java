package org.y1000.entities.creatures.event;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.AggressiveNpc;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;

public record SeekPlayerEvent(AggressiveNpc aggressiveNpc) implements EntityEvent {
    @Override
    public Entity source() {
        return aggressiveNpc;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }
}

package org.y1000.entities.creatures.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;

public record NpcCastCloneEvent(Npc npc, int number) implements EntityEvent {

    public NpcCastCloneEvent {
        Validate.isTrue(number > 0);
        Validate.notNull(npc);
    }

    @Override
    public Entity source() {
        return npc;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }
}

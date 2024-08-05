package org.y1000.entities.creatures.event;

import org.y1000.entities.Entity;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;

public final class NpcShiftEvent implements EntityEvent {

    private final String idName;
    private final Npc source;

    public NpcShiftEvent(String idName,
                         Npc source) {
        this.idName = idName;
        this.source = source;
    }

    public Npc npc() {
        return source;
    }

    public String shiftToName() {
        return idName;
    }

    @Override
    public Entity source() {
        return source;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }

    public RemoveEntityEvent createRemoveEvent() {
        return new RemoveEntityEvent(source);
    }

}

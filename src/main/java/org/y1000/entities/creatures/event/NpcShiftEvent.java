package org.y1000.entities.creatures.event;

import org.y1000.entities.Entity;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.npc.INpc;
import org.y1000.event.IEntityEvent;
import org.y1000.event.EntityEventVisitor;

public final class NpcShiftEvent implements IEntityEvent {

    private final String idName;
    private final INpc source;

    public NpcShiftEvent(String idName,
                         INpc source) {
        this.idName = idName;
        this.source = source;
    }

    public INpc npc() {
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

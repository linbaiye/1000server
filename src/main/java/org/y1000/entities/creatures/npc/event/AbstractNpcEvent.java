package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;

public abstract class AbstractNpcEvent implements NpcEvent {

    private final Npc npc;

    protected AbstractNpcEvent(Npc npc) {
        this.npc = npc;
    }

    @Override
    public Npc source() {
        return npc;
    }
}

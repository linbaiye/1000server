package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;

public class NpcMovedEvent extends AbstractNpcEvent {

    protected NpcMovedEvent(Npc npc) {
        super(npc);
    }
    @Override
    public void accept(NpcEventHandler handler) {
        handler.updateAOIAndNotifyPlayers(source());
    }

    public static NpcMovedEvent of(Npc npc) {
        return new NpcMovedEvent(npc);
    }
}

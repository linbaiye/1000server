package org.y1000.entities.npc.event;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.npc.Npc;
import org.y1000.realm.NpcEventHandler;

public class NpcCopyEvent extends AbstractNpcEvent {

    private final int copyNumber;

    private final ActiveEntity enemy;

    public NpcCopyEvent(Npc npc,
                        int copyNumber,
                        ActiveEntity enemy) {
        super(npc);
        this.copyNumber = copyNumber;
        this.enemy = enemy;
    }

    @Override
    public void accept(NpcEventHandler handler) {
        handler.copy(source(), copyNumber, enemy);
    }
}

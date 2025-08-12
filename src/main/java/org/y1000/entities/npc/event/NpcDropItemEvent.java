package org.y1000.entities.npc.event;

import org.y1000.entities.AbstractDropItemAbility;
import org.y1000.entities.npc.Npc;
import org.y1000.realm.NpcEventHandler;

import java.util.List;

public class NpcDropItemEvent extends AbstractNpcEvent {

    private final List<AbstractDropItemAbility.Item> items;

    public NpcDropItemEvent(Npc npc, List<AbstractDropItemAbility.Item> items) {
        super(npc);
        this.items = items;
    }

    @Override
    public void accept(NpcEventHandler handler) {
        items.forEach(item -> handler.dropItem(item.name(), item.number(), source().coordinate()));
    }
}

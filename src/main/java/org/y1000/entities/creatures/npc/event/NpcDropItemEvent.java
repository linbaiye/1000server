package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcDropItemAbility;
import org.y1000.realm.NpcEventHandler;

import java.util.List;

public class NpcDropItemEvent extends AbstractNpcEvent {

    private final List<NpcDropItemAbility.Item> items;

    public NpcDropItemEvent(Npc npc, List<NpcDropItemAbility.Item> items) {
        super(npc);
        this.items = items;
    }

    @Override
    public void accept(NpcEventHandler handler) {
        items.forEach(item -> handler.dropItem(item.name(), item.number(), source().coordinate()));
    }
}

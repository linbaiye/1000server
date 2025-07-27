package org.y1000.entities.creatures.npc;

import org.y1000.entities.AbstractDopItemAbility;
import org.y1000.entities.creatures.npc.event.NpcDropItemEvent;

import java.util.List;
import java.util.Optional;

public class NpcDropItemAbility extends AbstractDopItemAbility {
    private final List<Item> items;

    public NpcDropItemAbility(List<Item> items) {
        this.items = items;
    }

    public void apply(Npc npc) {
        npc.sendEvent(new NpcDropItemEvent(npc, items));
    }

    public static Optional<NpcDropItemAbility> parse(String dropText) {
        List<Item> list = parseItems(dropText);
        return list.isEmpty() ? Optional.empty() : Optional.of(new NpcDropItemAbility(list));
    }
}

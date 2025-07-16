package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.creatures.npc.event.NpcDropItemEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class NpcDropItemAbility {

    private final List<Item> items;

    private NpcDropItemAbility(List<Item> items) {
        this.items = items;
    }

    public record Item(String name, int number) { }

    public void apply(Npc npc) {
        npc.sendEvent(new NpcDropItemEvent(npc, items));
    }

    public static Optional<NpcDropItemAbility> parse(String dropText) {
        if (StringUtils.isEmpty(dropText))
            return Optional.empty();
        String[] tokens = dropText.split(":");
        List<Item> dropItems = new ArrayList<>();
        for (int i = 0; i < tokens.length / 3; i++) {
            int chance = Integer.parseInt(tokens[i * 3 + 1]);
            boolean drop = ThreadLocalRandom.current().nextInt(1, chance + 1) == chance;
            if (drop)
                dropItems.add(new Item(tokens[i * 3], Integer.parseInt(tokens[i * 3 + 2])));
        }
        return dropItems.isEmpty() ? Optional.empty() : Optional.of(new NpcDropItemAbility(dropItems));
    }

}

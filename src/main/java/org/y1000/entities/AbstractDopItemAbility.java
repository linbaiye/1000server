package org.y1000.entities;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractDopItemAbility {
    public record Item(String name, int number) { }

    public static List<Item> parseItems(String dropText) {
        if (StringUtils.isEmpty(dropText))
            return Collections.emptyList();
        String[] tokens = dropText.split(":");
        List<Item> dropItems = new ArrayList<>();
        for (int i = 0; i < tokens.length / 3; i++) {
            int chance = Integer.parseInt(tokens[i * 3 + 2]);
            boolean drop = ThreadLocalRandom.current().nextInt(1, chance + 1) == chance;
            if (drop)
                dropItems.add(new Item(tokens[i * 3], Integer.parseInt(tokens[i * 3 + 1])));
        }
        return dropItems;
    }

}

package org.y1000.inventory;

import java.util.HashSet;
import java.util.Set;

public final class Inventory {
    private final Set<Item> items = new HashSet<>();
    public void add(Item item) {
        items.add(item);
    }
}

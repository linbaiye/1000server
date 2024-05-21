package org.y1000.entities.players.inventory;

import org.y1000.entities.item.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class Inventory {

    private static final int MAX_SIZE = 30;

    private final Map<Integer, Item> items = new HashMap<>(30);

    public boolean isFull() {
        return items.size() >= MAX_SIZE;
    }

    public void foreach(BiConsumer<Integer, Item> consumer)  {
        items.forEach(consumer);
    }

    public void add(Item item) {
        if (isFull()) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < MAX_SIZE; i++) {
            if (!items.containsKey(i)) {
                items.put(i, item);
                break;
            }
        }
    }

    public void add(Collection<Item> items) {
        if (items.size() + this.items.size() > MAX_SIZE) {
            throw new IndexOutOfBoundsException();
        }
        items.forEach(this::add);
    }

    public int maxSize() {
        return MAX_SIZE;
    }

}

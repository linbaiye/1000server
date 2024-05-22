package org.y1000.entities.players.inventory;

import org.y1000.entities.item.Item;
import org.y1000.entities.item.StackItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

    public boolean add(Item item) {
        if (isFull()) {
            return false;
        }
        for (int i = 1; i <= MAX_SIZE; i++) {
            if (!items.containsKey(i)) {
                items.put(i, item);
                return true;
            }
        }
        return false;
    }

    public boolean swap(int from, int to) {
        if (!items.containsKey(from) && !items.containsKey(to)) {
            return false;
        }
        Item fromItem = items.remove(from);
        Item toItem = items.remove(to);
        if (fromItem != null)
            items.put(to, fromItem);
        if (toItem != null)
            items.put(from, toItem);
        return true;
    }

    public Item getItem(int slot) {
        return items.get(slot);
    }

    public void put(int slot, Item item) {
        Item current = items.get(slot);
        if (current == null) {
            items.put(slot, item);
            return;
        }
        if (current instanceof StackItem currentStackItem &&
                currentStackItem.canStack(item)) {
            currentStackItem.stack(item);
            return;
        }
        throw new UnsupportedOperationException("Slot " + slot  + " has item and can't stack.");
    }

    public int remove(Item item) {
        Iterator<Map.Entry<Integer, Item>> iterator = items.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Item> next = iterator.next();
            if (next.getValue().equals(item)) {
                iterator.remove();
                return next.getKey();
            }
        }
        return -1;
    }

    public boolean add(Collection<Item> items) {
        if (items.size() + this.items.size() > MAX_SIZE) {
            return false;
        }
        items.forEach(this::add);
        return true;
    }

    public int maxSize() {
        return MAX_SIZE;
    }

}

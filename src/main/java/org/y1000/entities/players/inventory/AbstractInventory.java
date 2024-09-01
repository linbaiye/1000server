package org.y1000.entities.players.inventory;

import org.apache.commons.lang3.Validate;
import org.y1000.item.Item;
import org.y1000.item.StackItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractInventory {

    private final Map<Integer, Item> items ;

    private final int capacity;
    public AbstractInventory(int cap) {
        Validate.isTrue(cap > 0);
        this.capacity = cap;
        items = new HashMap<>(cap);
    }
    public boolean isFull() {
        return items.size() >= capacity();
    }

    protected Map<Integer, Item> items() {
        return items;
    }

    public void foreach(BiConsumer<Integer, Item> consumer) {
        items.forEach(consumer);
    }

    public int capacity() {
        return capacity;
    }

    public int itemCount() {
        return items.size();
    }

    public int availableSlots() {
        return capacity() - itemCount();
    }

    public int add(Item item) {
        if (item == null) {
            return 0;
        }
        if (item instanceof StackItem stackItem) {
            var targetSlot = -1;
            for (int slot : items.keySet()) {
                Item slotItem = items.get(slot);
                if (slotItem instanceof StackItem slotStackItem && slotStackItem.containsSameItem(stackItem)) {
                    targetSlot = slot;
                    break;
                }
            }
            if (targetSlot != -1) {
                var slotStackItem = ((StackItem) items.get(targetSlot));
                if (slotStackItem.hasMoreSpace(stackItem.number())) {
                    items.put(targetSlot, slotStackItem.increase(stackItem.number()));
                    return targetSlot;
                }
                return 0;
            }
        }
        if (isFull()) {
            return 0;
        }
        for (int i = 1; i <= capacity(); i++) {
            if (!items.containsKey(i)) {
                items.put(i, item);
                return i;
            }
        }
        return 0;
    }

    public Item remove(int slot) {
        return items.remove(slot);
    }

    public Item remove(int slot, long number) {
        if (!hasEnough(slot, number)) {
            return null;
        }
        Item item = getItem(slot);
        decrease(slot, number);
        if (item instanceof StackItem stackItem) {
            return new StackItem(stackItem.item(), number);
        }
        return item;
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

    public boolean hasEnough(int slot, long number) {
        var item = getItem(slot);
        if (item == null) {
            return false;
        }
        if (item instanceof StackItem stackItem) {
            return stackItem.number() >= number;
        }
        return number == 1;
    }

    public boolean decrease(int slot, long number) {
        if (number <= 0)
            return false;
        Item item = items.get(slot);
        if (item == null) {
            return false;
        }
        if (item instanceof StackItem) {
            decreaseStack(slot, number);
        } else {
            items.remove(slot);
        }
        return true;
    }

    public void put(int slot, Item item) {
        Validate.notNull(item, "item must not be null.");
        var items = items();
        Item current = items.get(slot);
        if (current == null) {
            items.put(slot, item);
            return;
        }
        if (current instanceof StackItem stackItem && stackItem.canMerge(item)) {
            items.put(slot, stackItem.merge(item));
            return;
        }
        throw new UnsupportedOperationException("Slot " + slot + " has item already.");
    }

    protected void decreaseStack(int slot, long number) {
        var item = getItem(slot);
        if (item instanceof StackItem stackItem) {
            var decreased = stackItem.decrease(number);
            if (decreased.number() <= 0) {
                remove(slot);
            } else {
                items.put(slot, decreased);
            }
        }
    }

    public boolean decrease(int slotId) {
        return decrease(slotId, 1);
    }

    boolean canPut(Item item, int slot) {
        Item bankItem = getItem(slot);
        if (bankItem instanceof StackItem bankStackItem && item
                instanceof StackItem stackItem) {
            return bankStackItem.name().equals(stackItem.name()) &&
                    bankStackItem.hasMoreSpace(stackItem.number());
        }
        return bankItem == null;
    }

    public abstract boolean canPut(int slot, Item item);
}

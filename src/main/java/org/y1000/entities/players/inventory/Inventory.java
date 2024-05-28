package org.y1000.entities.players.inventory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.GroundedItem;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.entities.players.EventEmiter;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.InventorySlotSwappedEvent;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.clientevent.ClientDoubleClickSlotEvent;
import org.y1000.message.clientevent.ClientDropItemEvent;
import org.y1000.message.clientevent.ClientInventoryEvent;
import org.y1000.message.clientevent.ClientSwapInventoryEvent;

import java.util.*;
import java.util.function.BiConsumer;

@Slf4j
public final class Inventory {

    private static final int MAX_CAP = 30;

    private final Map<Integer, Item> items = new HashMap<>(30);

    public boolean isFull() {
        return items.size() >= MAX_CAP;
    }

    public void foreach(BiConsumer<Integer, Item> consumer)  {
        items.forEach(consumer);
    }

    public int maxCapacity() {
        return MAX_CAP;
    }

    public int count() {
        return items.size();
    }


    private Optional<StackItem> findStackItem(String name) {
        return items.values().stream()
                .filter(item -> item instanceof StackItem stackItem && stackItem.name().equals(name))
                .findFirst()
                .map(StackItem.class::cast);
    }


    public int add(Item item) {
        if (item instanceof StackItem stackItem) {
            for (int slot : items.keySet()) {
                Item slotItem = items.get(slot);
                if (slotItem instanceof StackItem currentSlot &&
                        slotItem.name().equals(item.name())) {
                    if (currentSlot.hasMoreCapacity(stackItem.number())) {
                        currentSlot.increase(stackItem.number());
                        return slot;
                    } else {
                        return 0;
                    }
                }
            }
        }
        if (isFull()) {
            return 0;
        }
        for (int i = 1; i <= MAX_CAP; i++) {
            if (!items.containsKey(i)) {
                items.put(i, item);
                return i;
            }
        }
        return 0;
    }

    public Item get(int slot) {
        return items.get(slot);
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
        Validate.notNull(item, "item must not be null.");
        Item current = items.get(slot);
        if (current == null) {
            items.put(slot, item);
            return;
        }
        throw new UnsupportedOperationException("Slot " + slot  + " has item already.");
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

    private void assertRange(int slot){
        Validate.isTrue(slot >= 1 && slot <= maxCapacity(), "Slot out of range.");
    }


    private boolean dropItem(int slot, int number) {
        Item item = items.get(slot);
        if (!(item instanceof StackItem stackItem)) {
            items.remove(slot);
            return true;
        }
        if (stackItem.number() < number) {
            return false;
        }
        stackItem.drop(number);
        if (stackItem.number() == 0) {
            items.remove(slot);
        }
        return true;
    }


    public boolean canPick(Item item) {
        if (item instanceof StackItem stackItem) {
            return findStackItem(item.name())
                    .map(current -> current.hasMoreCapacity(stackItem.number()))
                    .orElse(!isFull());
        }
        return !isFull();
    }

    public boolean canPick(GroundedItem item) {
        for (Item value : items.values()) {
            if (value instanceof StackItem stackItem
                    && stackItem.name().equals(item.getName())) {
                return stackItem.hasMoreCapacity(item.getNumber());
            }
        }
        return !isFull();
    }

    private void handleDropEvent(Player player, ClientDropItemEvent dropItemEvent, EventEmiter eventEmiter) {
        assertRange(dropItemEvent.sourceSlot());
        Item item = getItem(dropItemEvent.sourceSlot());
        if (item == null) {
            log.warn("Nothing to drop.");
            return;
        }
        if (dropItem(dropItemEvent.sourceSlot(), dropItemEvent.number())) {
            log.debug("Dropped item {}", item);
            var numberLeft = item instanceof StackItem stackItem ? stackItem.number() : 0;
            eventEmiter.emitEvent(new PlayerDropItemEvent(player, dropItemEvent, item.name(),
                    item instanceof StackItem ? dropItemEvent.number() : null, numberLeft));
        }
    }


    public void handleClientEvent(Player player, ClientInventoryEvent inventoryEvent, EventEmiter eventEmiter) {
        if (inventoryEvent instanceof ClientSwapInventoryEvent swapInventoryEvent &&
                swap(swapInventoryEvent.sourceSlot(), swapInventoryEvent.destinationSlot())) {
            eventEmiter.emitEvent(new InventorySlotSwappedEvent(player, swapInventoryEvent.sourceSlot(), swapInventoryEvent.destinationSlot()));
        } else if (inventoryEvent instanceof ClientDropItemEvent dropItemEvent) {
            handleDropEvent(player, dropItemEvent, eventEmiter);
        }
    }
}

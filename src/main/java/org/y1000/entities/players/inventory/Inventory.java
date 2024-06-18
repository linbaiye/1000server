package org.y1000.entities.players.inventory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.InventorySlotSwappedEvent;
import org.y1000.item.Weapon;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.clientevent.ClientDropItemEvent;
import org.y1000.message.clientevent.ClientInventoryEvent;
import org.y1000.message.clientevent.ClientSwapInventoryEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.util.UnaryAction;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Slf4j
public final class Inventory {

    private static final int MAX_CAP = 30;

    private final Map<Integer, Item> items = new HashMap<>(30);

    private final Map<Integer, Item> tradingItems = new HashMap<>(30);

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

    public void foreachTrading(BiConsumer<Integer, Item> consumer) {
        tradingItems.forEach(consumer);
    }

    public boolean tradeItem(int slot, int number) {
        if (!items.containsKey(slot) || tradingItems.containsKey(slot)) {
            return false;
        }
        Item item = items.get(slot);
        if (!(item instanceof StackItem stackItem)) {
            tradingItems.put(slot, item);
            return true;
        }
        if (stackItem.canSplit(number)) {
            StackItem split = stackItem.split(number);
            tradingItems.put(slot, split);
        }
        if (stackItem.number() == 0) {
            items.remove(slot);
        }
        return true;
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

    public Item remove(int slot) {
        return items.remove(slot);
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

    private <T extends Item> Optional<T> findFirst(Predicate<T> predicate, Class<T> type)  {
        return items.values().stream()
                .filter(i -> type.isAssignableFrom(i.getClass()))
                .map(type::cast)
                .filter(predicate)
                .findFirst();
    }


    public Optional<Weapon> findWeapon(AttackKungFuType type) {
        Objects.requireNonNull(type, "type can't be null.");
        return findFirst(weapon -> weapon.kungFuType() == type, Weapon.class);
    }

    public int findWeaponSlot(AttackKungFuType type) {
        Objects.requireNonNull(type, "type can't be null.");
        for (Integer i : items.keySet()) {
            if (items.get(i) instanceof Weapon weapon && weapon.kungFuType() == type) {
                return i;
            }
        }
        return 0;
    }

    public boolean contains(String name) {
        return items.values().stream().anyMatch(item -> item.name().equals(name));
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
        stackItem.decrease(number);
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


    private void handleDropEvent(Player player, ClientDropItemEvent dropItemEvent, UnaryAction<? super PlayerEvent> eventSender) {
        assertRange(dropItemEvent.sourceSlot());
        Item item = getItem(dropItemEvent.sourceSlot());
        if (item == null) {
            log.warn("Nothing to drop.");
            return;
        }
        if (dropItem(dropItemEvent.sourceSlot(), dropItemEvent.number())) {
            log.debug("Dropped item {}", item);
            var numberLeft = item instanceof StackItem stackItem ? stackItem.number() : 0;
            PlayerDropItemEvent event = new PlayerDropItemEvent(player, dropItemEvent, item.name(),
                    item instanceof StackItem ? dropItemEvent.number() : null, numberLeft);
            eventSender.invoke(event);
        }
    }

    public boolean consumeStackItem(Player player,
                                    String name,
                                    UnaryAction<? super PlayerEvent> eventSender) {
        Integer consumedSlot = null;
        boolean delete = false;
        for (Integer slot : items.keySet()) {
            if (items.get(slot).name().equals(name) &&
                    items.get(slot) instanceof StackItem stackItem) {
                delete = stackItem.decrease(1) == 0;
                consumedSlot = slot;
                break;
            }
        }
        if (delete) {
            items.remove(consumedSlot);
            eventSender.invoke(UpdateInventorySlotEvent.remove(player, consumedSlot));
        } else if (consumedSlot != null) {
            eventSender.invoke(UpdateInventorySlotEvent.update(player, consumedSlot, getItem(consumedSlot)));
        }
        return consumedSlot != null;
    }


    public void handleClientEvent(Player player, ClientInventoryEvent inventoryEvent, UnaryAction<? super PlayerEvent> eventSender) {
        if (inventoryEvent instanceof ClientSwapInventoryEvent swapInventoryEvent &&
                swap(swapInventoryEvent.sourceSlot(), swapInventoryEvent.destinationSlot())) {
            eventSender.invoke(new InventorySlotSwappedEvent(player, swapInventoryEvent.sourceSlot(), swapInventoryEvent.destinationSlot()));
        } else if (inventoryEvent instanceof ClientDropItemEvent dropItemEvent) {
            handleDropEvent(player, dropItemEvent, eventSender);
        }
    }
}

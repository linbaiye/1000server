package org.y1000.entities.players.inventory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.item.*;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.InventorySlotSwappedEvent;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.clientevent.ClientDropItemEvent;
import org.y1000.message.clientevent.ClientInventoryEvent;
import org.y1000.message.clientevent.ClientSwapInventoryEvent;
import org.y1000.event.EntityEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.trade.TradeItem;
import org.y1000.util.UnaryAction;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Slf4j
public final class Inventory {

    private static final int MAX_CAP = 30;

    private final Map<Integer, Item> items = new HashMap<>(30);

    private final Map<Integer, Item> tradingItems = new HashMap<>(30);

    public boolean isFull() {
        return items.size() >= MAX_CAP;
    }

    public void foreach(BiConsumer<Integer, Item> consumer) {
        items.forEach(consumer);
    }

    public int maxCapacity() {
        return MAX_CAP;
    }

    public int itemCount() {
        return items.size();
    }

    public int emptySlotSize() {
        return MAX_CAP - itemCount();
    }



    private Optional<DefaultStackItem> findStackItem(String name) {
        return items.values().stream()
                .filter(item -> item instanceof DefaultStackItem stackItem && stackItem.name().equals(name))
                .findFirst()
                .map(DefaultStackItem.class::cast);
    }


    public int add(Item item) {
        if (item == null) {
            return 0;
        }
        if (item instanceof DefaultStackItem stackItem) {
            for (int slot : items.keySet()) {
                Item slotItem = items.get(slot);
                if (slotItem instanceof DefaultStackItem currentSlot &&
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
        throw new UnsupportedOperationException("Slot " + slot + " has item already.");
    }

    private <T extends Item> Optional<T> findFirst(Predicate<T> predicate, Class<T> type) {
        return items.values().stream()
                .filter(i -> type.isAssignableFrom(i.getClass()))
                .map(type::cast)
                .filter(predicate)
                .findFirst();
    }

    public Optional<DefaultStackItem> findFirstStackItem(String name) {
        return findFirst(i -> i.name().equals(name), DefaultStackItem.class);
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

    private void assertRange(int slot) {
        Validate.isTrue(slot >= 1 && slot <= maxCapacity(), "Slot out of range.");
    }


    private boolean dropItem(int slot, int number) {
        Item item = items.get(slot);
        if (!(item instanceof DefaultStackItem stackItem)) {
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
        if (item instanceof DefaultStackItem stackItem) {
            return findStackItem(item.name())
                    .map(current -> current.hasMoreCapacity(stackItem.number()))
                    .orElse(!isFull());
        }
        return !isFull();
    }

    public boolean canPick(GroundedItem item) {
        for (Item value : items.values()) {
            if (value instanceof DefaultStackItem stackItem
                    && stackItem.name().equals(item.getName())) {
                return stackItem.hasMoreCapacity(item.getNumber());
            }
        }
        return !isFull();
    }

    public boolean canBuy(Collection<TradeItem> buyingItems, long cost) {
        Validate.notNull(buyingItems);
        for (TradeItem buyingItem : buyingItems) {
            assertRange(buyingItem.slotId());
            Item item = getItem(buyingItem.slotId());
            if (item == null) {
                continue;
            }
            if (item instanceof DefaultStackItem stackItem &&
                    stackItem.name().equals(buyingItem.name()) &&
                    stackItem.hasMoreCapacity(buyingItem.number())) {
                continue;
            }
            return false;
        }
        return findStackItem(DefaultStackItem.MONEY)
                .map(money -> money.number() >= cost)
                .orElse(false);
    }

    public boolean canSell(Collection<TradeItem> items) {
        Validate.notNull(items);
        for (TradeItem sellingItem : items) {
            assertRange(sellingItem.slotId());
            Item item = getItem(sellingItem.slotId());
            if (item == null || !item.name().equals(sellingItem.name())) {
                return false;
            }
            if (item instanceof DefaultStackItem stackItem && stackItem.number() < sellingItem.number()) {
                return false;
            }
        }
        return contains(DefaultStackItem.MONEY) || emptySlotSize() > 0;
    }

    public int findFirstSlot(String name) {
        for (Integer i : items.keySet()) {
            if (items.get(i).name().equals(name)) {
                return i;
            }
        }
        return 0;
    }

    public void buy(Collection<TradeItem> buyingItems, long cost, Player player, BiFunction<String, Long, Item> itemCreator) {
        Validate.notNull(player);
        Validate.notNull(itemCreator);
        Validate.isTrue(cost > 0);
        if (!canBuy(buyingItems, cost)) {
            throw new IllegalArgumentException();
        }
        for (TradeItem buyingItem : buyingItems) {
            Item item = getItem(buyingItem.slotId());
            if (item == null) {
                items.put(buyingItem.slotId(), itemCreator.apply(buyingItem.name(), (long)buyingItem.number()));
            } else if (item instanceof DefaultStackItem stackItem) {
                stackItem.increase(buyingItem.number());
            }
            player.emitEvent(new UpdateInventorySlotEvent(player, buyingItem.slotId(), getItem(buyingItem.slotId())));
        }
        int moneySlot = findFirstSlot(DefaultStackItem.MONEY);
        if (moneySlot != 0) {
            Item item = getItem(moneySlot);
            var current = ((DefaultStackItem)item).decrease(cost);
            if (current <= 0) {
                remove(moneySlot);
            }
            player.emitEvent(new UpdateInventorySlotEvent(player, moneySlot, getItem(moneySlot)));
        }
    }

    public boolean decrease(int slotId) {
        assertRange(slotId);
        Item item = getItem(slotId);
        if (item == null) {
            return false;
        }
        if (item instanceof AbstractStackItem stackItem) {
            if (stackItem.decrease(1) <= 0) {
                remove(slotId);
            }
        } else {
            remove(slotId);
        }
        return true;
    }

    public void sell(Collection<TradeItem> items, long profit, Player player) {
        Validate.notNull(player);
        Validate.isTrue(profit > 0);
        if (!canSell(items)) {
            throw new IllegalArgumentException();
        }
        for (TradeItem sellingItem : items) {
            Item item = getItem(sellingItem.slotId());
            if (item instanceof DefaultStackItem stackItem) {
                if (stackItem.decrease(sellingItem.number()) == 0) {
                    remove(sellingItem.slotId());
                }
            } else {
                remove(sellingItem.slotId());
            }
            player.emitEvent(new UpdateInventorySlotEvent(player, sellingItem.slotId(), getItem(sellingItem.slotId())));
        }
        findStackItem(DefaultStackItem.MONEY)
                .ifPresentOrElse(stackItem -> stackItem.increase(profit), () -> add(new DefaultStackItem(DefaultStackItem.MONEY, profit)));
        int n = findFirstSlot(DefaultStackItem.MONEY);
        player.emitEvent(new UpdateInventorySlotEvent(player, n, getItem(n)));
    }


    private void handleDropEvent(Player player, ClientDropItemEvent dropItemEvent, UnaryAction<EntityEvent> eventSender) {
        assertRange(dropItemEvent.sourceSlot());
        Item item = getItem(dropItemEvent.sourceSlot());
        if (item == null) {
            log.warn("Nothing to drop.");
            return;
        }
        if (dropItem(dropItemEvent.sourceSlot(), dropItemEvent.number())) {
            log.debug("Dropped item {}", item);
            UpdateInventorySlotEvent event = new UpdateInventorySlotEvent(player, dropItemEvent.sourceSlot(), getItem(dropItemEvent.sourceSlot()));
            eventSender.invoke(event);
            eventSender.invoke(new PlayerDropItemEvent(player, item.name(), dropItemEvent.number(), dropItemEvent.coordinate()));
            item.dropSound().ifPresent(s -> eventSender.invoke(new EntitySoundEvent(player, s)));
        }
    }

    public boolean consumeStackItem(Player player,
                                    String name,
                                    UnaryAction<? super PlayerEvent> eventSender) {
        Integer consumedSlot = null;
        boolean delete = false;
        for (Integer slot : items.keySet()) {
            if (items.get(slot).name().equals(name) &&
                    items.get(slot) instanceof DefaultStackItem stackItem) {
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


    public void handleClientEvent(Player player, ClientInventoryEvent inventoryEvent, UnaryAction<EntityEvent> eventSender) {
        if (inventoryEvent instanceof ClientSwapInventoryEvent swapInventoryEvent &&
                swap(swapInventoryEvent.sourceSlot(), swapInventoryEvent.destinationSlot())) {
            eventSender.invoke(new InventorySlotSwappedEvent(player, swapInventoryEvent.sourceSlot(), swapInventoryEvent.destinationSlot()));
        } else if (inventoryEvent instanceof ClientDropItemEvent dropItemEvent) {
            handleDropEvent(player, dropItemEvent, eventSender);
        }
    }
}

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

    private final Map<Integer, Item> items = new HashMap<>(MAX_CAP);

    public boolean isFull() {
        return items.size() >= MAX_CAP;
    }

    public void foreach(BiConsumer<Integer, Item> consumer) {
        items.forEach(consumer);
    }

    public int capacity() {
        return MAX_CAP;
    }

    public int itemCount() {
        return items.size();
    }

    public int availableSlots() {
        return MAX_CAP - itemCount();
    }

    private Optional<StackItem> findStackItem(String name) {
        return items.values().stream()
                .filter(item -> item instanceof StackItem stackItem && stackItem.name().equals(name))
                .findFirst()
                .map(StackItem.class::cast);
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
            if (targetSlot != -1)  {
                var slotStackItem = ((StackItem)items.get(targetSlot));
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

    public <T extends Item> Optional<StackItem> getStackItem(int slot, Class<T> type) {
        Validate.notNull(type);
        Item item = getItem(slot);
        return item instanceof StackItem stackItem && stackItem.origin(type).isPresent() ?
                Optional.of(stackItem) : Optional.empty();
    }

    public <T extends Item> Optional<T> getItem(int slot, Class<T> type) {
        Validate.notNull(type);
        Item item = getItem(slot);
        return item != null && type.isAssignableFrom(item.getClass()) ?
                Optional.of(type.cast(item)) : Optional.empty();
    }

    public Optional<StackItem> findFirstStackItem(String name) {
        return findFirst(i -> i.name().equals(name), StackItem.class);
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
        Validate.isTrue(slot >= 1 && slot <= capacity(), "Slot out of range.");
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


    public boolean canPick(GroundedItem item) {
        for (Item value : items.values()) {
            if (value instanceof StackItem stackItem
                    && stackItem.name().equals(item.getName())) {
                return stackItem.hasMoreSpace(item.getNumber());
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
            if (item instanceof StackItem stackItem &&
                    stackItem.name().equals(buyingItem.name()) &&
                    stackItem.hasMoreSpace(buyingItem.number())) {
                continue;
            }
            return false;
        }
        return findStackItem(ItemType.MONEY_NAME)
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
            if (item instanceof StackItem stackItem && stackItem.number() < sellingItem.number()) {
                return false;
            }
        }
        return contains(ItemType.MONEY_NAME) || availableSlots() > 0;
    }

    private int requiredSpace(List<StackItem> items) {
        int count = items.size();
        for (StackItem item : items) {
            int firstSlot = findFirstSlot(item.name());
            if (firstSlot == 0) {
                continue;
            }
            if (getItem(firstSlot) instanceof StackItem stackItem &&
                    stackItem.hasMoreSpace(item.number())) {
                count--;
            }
        }
        return count;
    }

    public boolean canTakeAll(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return true;
        }
        List<StackItem> stackItems = items.stream()
                .filter(i -> i instanceof StackItem)
                .map(StackItem.class::cast)
                .toList();
        int space = requiredSpace(stackItems);
        var nonStackSize = items.size() - stackItems.size();
        return nonStackSize + space <= availableSlots();
    }

    private int findFirstSlot(Predicate<? super Item> predicate) {
        for (int i = 1; i <= MAX_CAP; i++) {
            if (items.containsKey(i) && predicate.test(items.get(i))) {
                return i;
            }
        }
        return 0;
    }

    public int findFirstSlot(String name) {
        return name != null ? findFirstSlot(i -> i.name().equals(name)) : 0;
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
            } else if (item instanceof StackItem stackItem) {
                items.put(buyingItem.slotId(), stackItem.increase(buyingItem.number()));
            }
            player.emitEvent(new UpdateInventorySlotEvent(player, buyingItem.slotId(), getItem(buyingItem.slotId())));
        }
        int moneySlot = findFirstSlot(ItemType.MONEY_NAME);
        if (moneySlot != 0) {
            decreaseStack(moneySlot, cost);
            player.emitEvent(new UpdateInventorySlotEvent(player, moneySlot, getItem(moneySlot)));
        }
    }

    private void decreaseStack(int slot, long number) {
        var item =  getItem(slot);
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

    public void sell(Collection<TradeItem> items, StackItem profit, Player player) {
        Validate.notNull(player);
        Validate.notNull(profit);
        Validate.isTrue(profit.number() > 0);
        if (!canSell(items)) {
            throw new IllegalArgumentException();
        }
        for (TradeItem sellingItem : items) {
            Item item = getItem(sellingItem.slotId());
            if (item instanceof StackItem) {
                decreaseStack(sellingItem.slotId(), sellingItem.number());
            } else {
                remove(sellingItem.slotId());
            }
            player.emitEvent(new UpdateInventorySlotEvent(player, sellingItem.slotId(), getItem(sellingItem.slotId())));
        }
        add(profit);
        int n = findFirstSlot(ItemType.MONEY_NAME);
        player.emitEvent(new UpdateInventorySlotEvent(player, n, getItem(n)));
    }


    private void handleDropEvent(Player player, ClientDropItemEvent dropItemEvent, UnaryAction<EntityEvent> eventSender) {
        assertRange(dropItemEvent.sourceSlot());
        Item item = getItem(dropItemEvent.sourceSlot());
        if (item == null) {
            log.warn("Nothing to drop.");
            return;
        }
        if (decrease(dropItemEvent.sourceSlot(), dropItemEvent.number())) {
            log.debug("Dropped item {}", item);
            UpdateInventorySlotEvent event = new UpdateInventorySlotEvent(player, dropItemEvent.sourceSlot(), getItem(dropItemEvent.sourceSlot()));
            eventSender.invoke(event);
            eventSender.invoke(new PlayerDropItemEvent(player, item.name(), dropItemEvent.number(), dropItemEvent.coordinate()));
            item.dropSound().ifPresent(s -> eventSender.invoke(new EntitySoundEvent(player, s)));
        }
    }

    public boolean contains(ItemType type) {
        return items.values().stream().anyMatch(i -> i.itemType() == type);
    }


    private Item doConsumeStackItem(int targetSlot, Player player, UnaryAction<? super PlayerEvent> eventSender) {
        if (targetSlot == 0) {
            return null;
        }
        var stackItem = ((StackItem)items.get(targetSlot));
        var decreased = stackItem.decrease(1);
        if (decreased.number() == 0) {
            items.remove(targetSlot);
            eventSender.invoke(UpdateInventorySlotEvent.remove(player, targetSlot));
        } else {
            items.put(targetSlot, decreased);
            eventSender.invoke(UpdateInventorySlotEvent.update(player, targetSlot, getItem(targetSlot)));
        }
        return stackItem.item();

    }

    public Item consumeStackItem(Player player,
                                    ItemType type,
                                    UnaryAction<? super PlayerEvent> eventSender) {
        int slot = findFirstSlot(item -> item.itemType() == type);
        return doConsumeStackItem(slot, player, eventSender);
    }


    public boolean consumeStackItem(Player player,
                                    String name,
                                    UnaryAction<? super PlayerEvent> eventSender) {
        var targetSlot = findFirstSlot(i -> i.name().equals(name) && i instanceof StackItem);
        return doConsumeStackItem(targetSlot, player, eventSender) != null;
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

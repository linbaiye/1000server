package org.y1000.entities.players.inventory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.event.UpdateInventorySlotMessage;
import org.y1000.item.*;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.trade.TradeItem;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Slf4j
public final class Inventory extends AbstractInventory {

    private static final int MAX_CAP = 30;

    public Inventory() {
        super(MAX_CAP);
    }

    private <T extends Item> Optional<T> findFirst(Predicate<T> predicate, Class<T> type) {
        return items().values().stream()
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

    public Optional<Weapon> findWeapon(AttackKungFuType type) {
        Objects.requireNonNull(type, "type can't be null.");
        return findFirst(weapon -> weapon.kungFuType() == type, Weapon.class);
    }

    public int findWeaponSlot(AttackKungFuType type) {
        Objects.requireNonNull(type, "type can't be null.");
        var items = items();
        for (Integer i : items.keySet()) {
            if (items.get(i) instanceof Weapon weapon && weapon.kungFuType() == type) {
                return i;
            }
        }
        return 0;
    }

    public boolean contains(String name) {
        return items().values().stream().anyMatch(item -> item.name().equals(name));
    }

    public void add(int slot, Item item) {
        doAdd(slot, item);
    }

    public boolean hasEnough(String name, int number) {
        if (StringUtils.isEmpty(name) || number <= 0) {
            return false;
        }
        int slot = findFirstSlot(name);
        return slot != 0 && hasEnough(slot, number);
    }

    public boolean hasEnoughMoney(int number) {
        return hasEnough("钱币", number);
    }

    /**
     * Consume item by name and number.
     * @param name item name.
     * @param number item number.
     * @return the slot if consumed, 0 if not.
     */
    public int decrease(String name, int number) {
        if (!hasEnough(name, number)) {
            return 0;
        }
        int firstSlot = findFirstSlot(name);
        return remove(firstSlot, number) != null ? firstSlot : 0;
    }

    public int decreaseMoney(int number) {
        return decrease("钱币", number);
    }


    private int findFirstSlot(Predicate<? super Item> predicate) {
        var items = items();
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



    public boolean contains(ItemType type) {
        return items().values().stream().anyMatch(i -> i.itemType() == type);
    }


    private Item doConsumeStackItem(int targetSlot) {
        if (targetSlot == 0) {
            return null;
        }
        var items = items();
        var stackItem = ((StackItem)items.get(targetSlot));
        var decreased = stackItem.decrease(1);
        if (decreased.number() == 0) {
            items.remove(targetSlot);
        } else {
            items.put(targetSlot, decreased);
        }
        return stackItem.item();
    }


    public Item consumeStackItem(Player player,
                                 ItemType type) {
        int slot = findFirstSlot(item -> item.itemType() == type);
        if (slot == 0)
            return null;
        Item item = doConsumeStackItem(slot);
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
        return item;
    }


    @Override
    public boolean isFull() {
        return items().size() >= capacity();
    }

    @Override
    public boolean canPut(int slot, Item item) {
        return slot > 0 && slot <= capacity()
                && canPut(item, slot);
    }
}

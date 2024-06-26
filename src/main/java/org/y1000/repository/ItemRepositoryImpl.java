package org.y1000.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.GroundedItem;
import org.y1000.item.*;

import java.util.NoSuchElementException;

public class ItemRepositoryImpl implements ItemRepository, ItemFactory {

    private final ItemSdbImpl itemSdb;

    public ItemRepositoryImpl(ItemSdbImpl itemSdb) {
        this.itemSdb = itemSdb;
    }



    @Override
    public void save(long playerId, int slot, Item item) {

    }

    private AbstractEquipment createEquipment(String name) {
        return switch (itemSdb.getEquipmentType(name)) {
            case WEAPON -> new Weapon(name, itemSdb.getAttackKungFuType(name));
            case HAT -> new Hat(name);
            case CHEST -> new Chest(name);
            default -> throw new NotImplementedException();
        };
    }


    @Override
    public Item createItem(GroundedItem item) {
        Validate.notNull(item, "item must not be null");
        if (item.getNumber() != null) {
            return createItem(item.getName(), item.getNumber());
        } else {
            return createItem(item.getName());
        }
    }

    @Override
    public Item createItem(String name) {
        if (!itemSdb.contains(name)) {
            throw new NoSuchElementException(name + " is not a valid item.");
        }
        return switch (itemSdb.getType(name)) {
            case EQUIPMENT -> createEquipment(name);
            default -> throw new NoSuchElementException();
        };
    }

    @Override
    public Item createItem(String name, int number) {
        if (!itemSdb.contains(name)) {
            throw new NoSuchElementException(name + " is not a valid item.");
        }
        return switch (itemSdb.getType(name)) {
            case ARROW -> new StackItem(name, number, ItemType.ARROW);
            case SELLING_GOODS -> new StackItem(name, number, ItemType.SELLING_GOODS);
            case EQUIPMENT -> createItem(name);
            default -> throw new NoSuchElementException();
        };
    }
}

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

    /*
                    .trouser(new Trouser("女子短裙"))
                .clothing(new Clothing("女子上衣"))
                .boot(new Boot("女子皮鞋"))
                .hat(new Hat("女子血魔头盔"))
                .chest(new Chest("女子雨中客道袍"))
                .hair(new Hair("女子麻花辫", false))
                .wrist(new Wrist("女子太极护腕"))
     */

    public Equipment createEquipment(String name) {
        return switch (itemSdb.getEquipmentType(name)) {
            case WEAPON -> new Weapon(name, itemSdb.getAttackKungFuType(name));
            case HAT -> createHat(name);
            case CHEST -> createChest(name);
            case TROUSER -> createTrouser(name);
            case CLOTHING -> createClothing(name);
            case BOOT -> createBoot(name);
            case HAIR -> createHair(name);
            case WRIST -> createWrist(name);
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
    public Item createItem(String name, long number) {
        if (!itemSdb.contains(name)) {
            throw new NoSuchElementException(name + " is not a valid item.");
        }
        return switch (itemSdb.getType(name)) {
            case ARROW -> new DefaultStackItem(name, number, ItemType.ARROW);
            case SELLING_GOODS -> new DefaultStackItem(name, number, ItemType.SELLING_GOODS);
            case EQUIPMENT -> createEquipment(name);
            case MONEY -> DefaultStackItem.money(number);
            case PILL -> new Pill(name, number, new PillAttributeProviderImpl());
            default -> throw new NoSuchElementException();
        };
    }

    @Override
    public Trouser createTrouser(String name) {
        return new Trouser(name, itemSdb.isMale(name));
    }

    @Override
    public Hat createHat(String name) {
        return new Hat(name, itemSdb.isMale(name));
    }

    @Override
    public Chest createChest(String name) {
        return new Chest(name, itemSdb.isMale(name));
    }

    @Override
    public Hair createHair(String name) {
        return new Hair(name, itemSdb.isMale(name));
    }

    @Override
    public Boot createBoot(String name) {
        return new Boot(name, itemSdb.isMale(name));
    }

    @Override
    public Wrist createWrist(String name) {
        return new Wrist(name, itemSdb.isMale(name));
    }

    @Override
    public Clothing createClothing(String name) {
        return new Clothing(name, itemSdb.isMale(name));
    }
}

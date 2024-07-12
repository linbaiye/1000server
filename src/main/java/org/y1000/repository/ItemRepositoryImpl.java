package org.y1000.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.GroundedItem;
import org.y1000.item.*;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.sdb.ItemDrugSdb;

import java.util.NoSuchElementException;

public final class ItemRepositoryImpl implements ItemRepository, ItemFactory {

    private final ItemSdbImpl itemSdb;
    private final ItemDrugSdb itemDrugSdb;
    private final KungFuFactory kungFuFactory;

    public ItemRepositoryImpl(ItemSdbImpl itemSdb,
                              ItemDrugSdb itemDrugSdb,
                              KungFuFactory kungFuFactory) {
        this.itemSdb = itemSdb;
        this.itemDrugSdb = itemDrugSdb;
        this.kungFuFactory = kungFuFactory;
    }



    @Override
    public void save(long playerId, int slot, Item item) {

    }

    public Equipment createEquipment(String name) {
        return switch (itemSdb.getEquipmentType(name)) {
            case WEAPON -> new Weapon(name, itemSdb);
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
            default -> DefaultItem.builder().name(name).dropSound(itemSdb.getSoundDrop(name)).eventSound(itemSdb.getSoundEvent(name))
                    .desc(itemSdb.getDesc(name))
                    .build();
        };
    }

    private KungFuItem createKungFuItem(String name, long number) {
        return KungFuItem.builder()
                .eventSound(itemSdb.getSoundEvent(name))
                .dropSound(itemSdb.getSoundDrop(name))
                .name(name)
                .number(number)
                .kungFu(kungFuFactory.create(name))
                .desc(itemSdb.getDesc(name))
                .build();
    }

    @Override
    public Item createItem(String name, long number) {
        if (!itemSdb.contains(name)) {
            throw new NoSuchElementException(name + " is not a valid item.");
        }

        if (!ItemType.contains(itemSdb.getTypeValue(name))) {
            return itemSdb.canStack(name) ? new DefaultStackItem(name, number, ItemType.STACK, itemSdb) :
                    new DefaultItem(name, itemSdb.getSoundDrop(name), itemSdb.getSoundEvent(name), itemSdb.getDesc(name));
        }
        if (!itemSdb.canStack(name)) {
            return createItem(name);
        }
        return switch (itemSdb.getType(name)) {
            case DYE -> new Dye(name, number, itemSdb);
            case ARROW -> new DefaultStackItem(name, number, ItemType.ARROW, itemSdb);
            case SELLING_GOODS -> new DefaultStackItem(name, number, ItemType.SELLING_GOODS, itemSdb);
            case MONEY -> new DefaultStackItem(name, number, ItemType.MONEY, itemSdb);
            case PILL -> new Pill(name, number, new PillAttributeProviderImpl(name, itemSdb, itemDrugSdb));
            case KUNGFU -> createKungFuItem(name, number);
            case BANK_INVENTORY -> new BankInventory(name, number, itemSdb);
            default-> new DefaultStackItem(name, number, ItemType.STACK, itemSdb);
        };
    }

    @Override
    public Trouser createTrouser(String name) {
        return new Trouser(name, itemSdb);
    }

    @Override
    public Hat createHat(String name) {
        return new Hat(name, new DefaultArmorAttributeProvider(name, itemSdb));
    }

    @Override
    public Chest createChest(String name) {
        return new Chest(name, new DefaultArmorAttributeProvider(name, itemSdb));
    }

    @Override
    public Hair createHair(String name) {
        return new Hair(name, itemSdb);
    }

    @Override
    public Boot createBoot(String name) {
        return new Boot(name, new DefaultArmorAttributeProvider(name, itemSdb));
    }

    @Override
    public Wrist createWrist(String name) {
        return new Wrist(name, new DefaultArmorAttributeProvider(name, itemSdb));
    }

    @Override
    public Clothing createClothing(String name) {
        return Clothing.builder()
                .name(name)
                .male(itemSdb.isMale(name))
                .dropSound(itemSdb.getSoundDrop(name))
                .eventSound(itemSdb.getSoundEvent(name))
                .desc(itemSdb.getDesc(name))
                .build();
    }
}

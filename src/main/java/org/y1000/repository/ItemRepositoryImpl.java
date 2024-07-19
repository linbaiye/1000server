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
            case WEAPON -> new WeaponImpl(name, itemSdb);
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


    private Item create(String name) {
        if (!itemSdb.contains(name)) {
            throw new NoSuchElementException(name + " is not a valid item.");
        }
        if (!ItemType.contains(itemSdb.getTypeValue(name))) {
            return SimpleItem.uncategoried(name, itemSdb);
        }
        ItemType type = itemSdb.getType(name);
        return switch (type) {
            case EQUIPMENT -> createEquipment(name);
            case ARROW, MONEY, SELLING_GOODS, KNIFE -> new SimpleItem(name, type, itemSdb);
            case DYE -> new Dye(name, itemSdb);
            case PILL -> new Pill(name, new PillAttributeProviderImpl(name, itemSdb, itemDrugSdb));
            case KUNGFU -> createKungFuItem(name);
            case BANK_INVENTORY -> new BankInventory(name, itemSdb);
            default -> SimpleItem.uncategoried(name, itemSdb);
        };
    }

    @Override
    public Item createItem(String name) {
        return createItem(name, 1);
    }

    private KungFuItem createKungFuItem(String name) {
        return KungFuItem.builder()
                .eventSound(itemSdb.getSoundEvent(name))
                .dropSound(itemSdb.getSoundDrop(name))
                .name(name)
                .kungFu(kungFuFactory.create(name))
                .desc(itemSdb.getDesc(name))
                .build();
    }

    @Override
    public Item createItem(String name, long number) {
        Validate.notNull(name);
        Validate.isTrue(number > 0);
        var item = create(name);
        return itemSdb.canStack(name) ? new StackItem(item, number) : item;
    }

    @Override
    public Trouser createTrouser(String name) {
        return new Trouser(name, itemSdb);
    }

    @Override
    public ArmorEquipment createHat(String name) {
        return new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.HAT);
    }

    @Override
    public ArmorEquipment createChest(String name) {
        return new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.CHEST);
    }

    @Override
    public Hair createHair(String name) {
        return new Hair(name, itemSdb);
    }

    @Override
    public ArmorEquipment createBoot(String name) {
        return new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.BOOT);
    }

    @Override
    public ArmorEquipment createWrist(String name) {
        return new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.WRIST);
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

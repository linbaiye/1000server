package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.item.*;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.persistence.ItemPo;
import org.y1000.sdb.ItemDrugSdb;

import java.util.ArrayList;
import java.util.List;
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


    public Equipment createEquipment(String name) {
        return switch (itemSdb.getEquipmentType(name)) {
            case WEAPON -> new WeaponImpl(name, itemSdb);
            case HAT -> createHat(name);
            case CHEST -> createChest(name);
            case TROUSER -> createTrouser(name);
            case CLOTHING -> createClothing(name);
            case BOOT -> createBoot(name);
            case HAIR -> createHair(name);
            case WRIST, WRIST_CHESTED -> createWrist(name);
        };
    }

    @Override
    public Item createItem(GroundedItem item) {
        Validate.notNull(item, "item must not be null");
        Item restored = item.getNumber() != null ?
                createItem(item.getName(), item.getNumber()) : createItem(item.getName());
        if (restored instanceof DyableEquipment dyableEquipment) {
            dyableEquipment.dye(item.getColor());
        }
        return restored;
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
            case ARROW, KNIFE -> new Ammo(name, type, itemSdb);
            case MONEY, SELLING_GOODS -> new SimpleItem(name, type, itemSdb);
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
    public SexualEquipment createTrouser(String name) {
        return itemSdb.isColoring(name) ? new DecorativeEquipment(name, EquipmentType.TROUSER, itemSdb, itemSdb.getColor(name))
                : new Trouser(name, itemSdb);
    }

    @Override
    public ArmorEquipment createHat(String name) {
        return itemSdb.isColoring(name) ?
                new DyableArmorEquipment(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.HAT, itemSdb.getColor(name)) :
                new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.HAT);
    }

    @Override
    public ArmorEquipment createChest(String name) {
        return itemSdb.isColoring(name) ?
                new DyableArmorEquipment(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.CHEST, itemSdb.getColor(name)) :
                new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.CHEST);
    }

    @Override
    public SexualEquipment createHair(String name) {
        Validate.isTrue(name != null && itemSdb.getType(name) == ItemType.EQUIPMENT);
        return itemSdb.isColoring(name) ? new DecorativeEquipment(name, EquipmentType.HAIR, itemSdb, itemSdb.getColor(name)) :
            new Hair(name, itemSdb);
    }

    @Override
    public ArmorEquipment createBoot(String name) {
        Validate.isTrue(name != null && itemSdb.getType(name) == ItemType.EQUIPMENT);
        return itemSdb.isColoring(name) ?
                new DyableArmorEquipment(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.BOOT, itemSdb.getColor(name)) :
                new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.BOOT);
    }

    @Override
    public ArmorEquipment createWrist(String name) {
        return itemSdb.isColoring(name) ?
                new DyableArmorEquipment(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.WRIST, itemSdb.getColor(name)) :
                new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.WRIST);
    }

    @Override
    public SexualEquipment createClothing(String name) {
        Validate.isTrue(name != null && itemSdb.getType(name) == ItemType.EQUIPMENT);
        return itemSdb.isColoring(name) ? new DecorativeEquipment(name, EquipmentType.CLOTHING, itemSdb, itemSdb.getColor(name))
                : new Clothing(name, itemSdb);
    }

    @Override
    public void save(EntityManager entityManager, Player player) {
        Validate.notNull(entityManager);
        Validate.notNull(player);
        entityManager.createQuery("delete from ItemPo i where i.itemKey.playerId = ?1")
                .setParameter(1, player.id()).executeUpdate();
        player.inventory().foreach((slot, item) -> entityManager.persist(ItemPo.convert(player.id(), slot, item)));
    }
}

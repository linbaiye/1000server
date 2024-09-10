package org.y1000.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.*;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.persistence.BankPo;
import org.y1000.persistence.ItemPo;
import org.y1000.sdb.ItemDrugSdb;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ItemRepositoryImpl implements ItemRepository, ItemFactory, BankRepository {

    private final ItemSdbImpl itemSdb;
    private final ItemDrugSdb itemDrugSdb;
    private final KungFuFactory kungFuFactory;
    private final EntityManagerFactory entityManagerFactory;

    public ItemRepositoryImpl(ItemSdbImpl itemSdb,
                              ItemDrugSdb itemDrugSdb,
                              KungFuFactory kungFuFactory,
                              EntityManagerFactory entityManagerFactory) {
        this.itemSdb = itemSdb;
        this.itemDrugSdb = itemDrugSdb;
        this.kungFuFactory = kungFuFactory;
        this.entityManagerFactory = entityManagerFactory;
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
    public Equipment createEquipment(String name, int color) {
        Validate.notNull(name);
        return switch (itemSdb.getEquipmentType(name)) {
            case WEAPON -> new WeaponImpl(name, itemSdb);
            case HAT -> createHat(name, color);
            case CHEST -> createChest(name, color);
            case TROUSER -> createTrouser(name, color);
            case CLOTHING -> createClothing(name, color);
            case BOOT -> createBoot(name, color);
            case HAIR -> createHair(name, color);
            case WRIST, WRIST_CHESTED -> createWrist(name, color);
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

    private SexualEquipment createTrouser(String name, int color) {
        return itemSdb.isColoring(name) ? new DecorativeEquipment(name, EquipmentType.TROUSER, itemSdb, color)
                : new Trouser(name, itemSdb);
    }

    @Override
    public SexualEquipment createTrouser(String name) {
        return createTrouser(name, itemSdb.getColorOrZero(name));
    }

    private ArmorEquipment createHat(String name, int color) {
        return itemSdb.isColoring(name) ?
                new DyableArmorEquipment(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.HAT, color) :
                new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.HAT);
    }

    @Override
    public ArmorEquipment createHat(String name) {
        return createHat(name, itemSdb.getColorOrZero(name));
    }

    private ArmorEquipment createChest(String name, int color) {
        return itemSdb.isColoring(name) ?
                new DyableArmorEquipment(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.CHEST, color) :
                new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.CHEST);
    }

    @Override
    public ArmorEquipment createChest(String name) {
        return createChest(name, itemSdb.getColorOrZero(name));
    }

    private SexualEquipment createHair(String name, int color) {
        Validate.isTrue(name != null && itemSdb.getType(name) == ItemType.EQUIPMENT);
        return itemSdb.isColoring(name) ? new DecorativeEquipment(name, EquipmentType.HAIR, itemSdb, color) :
                new Hair(name, itemSdb);
    }

    @Override
    public SexualEquipment createHair(String name) {
        return createHair(name, itemSdb.getColorOrZero(name));
    }


    private ArmorEquipment createBoot(String name, int color) {
        Validate.isTrue(name != null && itemSdb.getType(name) == ItemType.EQUIPMENT);
        return itemSdb.isColoring(name) ?
                new DyableArmorEquipment(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.BOOT, color) :
                new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.BOOT);
    }

    @Override
    public ArmorEquipment createBoot(String name) {
        return createBoot(name, itemSdb.getColorOrZero(name));
    }

    private ArmorEquipment createWrist(String name, int color) {
        return itemSdb.isColoring(name) ?
                new DyableArmorEquipment(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.WRIST, color) :
                new ArmorEquipmentImpl(name, new DefaultArmorAttributeProvider(name, itemSdb), EquipmentType.WRIST);
    }

    @Override
    public ArmorEquipment createWrist(String name) {
        return createWrist(name, itemSdb.getColorOrZero(name));
    }

    private SexualEquipment createClothing(String name, int color) {
        Validate.isTrue(name != null && itemSdb.getType(name) == ItemType.EQUIPMENT);
        return itemSdb.isColoring(name) ? new DecorativeEquipment(name, EquipmentType.CLOTHING, itemSdb, color)
                : new Clothing(name, itemSdb);
    }

    @Override
    public SexualEquipment createClothing(String name) {
        Validate.isTrue(name != null && itemSdb.getType(name) == ItemType.EQUIPMENT);
        return createClothing(name, itemSdb.getColorOrZero(name));
    }

    private void deleteItems(EntityManager entityManager, long playerId, ItemPo.Type type) {
        entityManager.createQuery("delete from ItemPo i where i.itemKey.playerId = ?1 and i.itemKey.type = ?2")
                .setParameter(1, playerId)
                .setParameter(2, type)
                .executeUpdate();
    }

    @Override
    public void save(EntityManager entityManager, Player player) {
        Validate.notNull(entityManager);
        Validate.notNull(player);
        deleteItems(entityManager, player.id(), ItemPo.Type.INVENTORY);
        player.inventory().foreach((slot, item) -> entityManager.persist(ItemPo.toInventoryItem(player.id(), slot, item)));
    }

    private Item restore(ItemPo itemPo) {
        var type = itemSdb.getTypeValue(itemPo.getName());
        if (ItemType.contains(type) && type == ItemType.EQUIPMENT.value()) {
            return createEquipment(itemPo.getName(), itemPo.getColor());
        } else {
            return createItem(itemPo.getName(), itemPo.getNumber());
        }
    }

    private Map<Integer, Item> findItems(EntityManager entityManager, long playerId, ItemPo.Type type) {
        return entityManager.createQuery("select i from ItemPo i where i.itemKey.playerId = ?1 and i.itemKey.type = ?2", ItemPo.class)
                .setParameter(1, playerId)
                .setParameter(2, type)
                .getResultStream()
                .collect(Collectors.toMap(i -> i.getItemKey().getSlot(), this::restore));
    }

    @Override
    public Inventory findInventory(EntityManager entityManager, long playerId) {
        Validate.notNull(entityManager);
        Map<Integer, Item> items = findItems(entityManager, playerId, ItemPo.Type.INVENTORY);
        Inventory inventory = new Inventory();
        items.forEach(inventory::put);
        return inventory;
    }

    private void merge(EntityManager entityManager, BankPo bankPo, Bank bank) {
        bankPo.setUnlocked(bank.getUnlocked());
        bankPo.setCapacity(bank.capacity());
        bank.foreach((slot, item) -> entityManager.persist(ItemPo.toBankItem(bankPo.getPlayerId(), slot, item)));
    }

    private void persist(EntityManager entityManager, long playerId, Bank bank) {
        BankPo bankPo = new BankPo(null, playerId, bank.capacity(), bank.getUnlocked());
        entityManager.persist(bankPo);
        bank.foreach((slot, item) -> entityManager.persist(ItemPo.toBankItem(bankPo.getPlayerId(), slot, item)));
    }

    @Override
    public void save(long playerId, Bank bank) {
        Validate.notNull(bank);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            tx.begin();
            deleteItems(entityManager, playerId, ItemPo.Type.BANK);
            findBankPo(entityManager, playerId)
                    .ifPresentOrElse(bankPo -> merge(entityManager, bankPo, bank),
                            () -> persist(entityManager, playerId, bank));
            tx.commit();
        }
    }

    private Optional<BankPo> findBankPo(EntityManager em, long playerId) {
        return em.createQuery("select b from BankPo b where b.playerId = ?1", BankPo.class)
                .setParameter(1, playerId)
                .getResultStream()
                .findFirst();

    }

    private Bank convert(EntityManager entityManager, BankPo bankPo) {
        Bank bank = new Bank(bankPo.getCapacity(), bankPo.getUnlocked());
        Map<Integer, Item> items = findItems(entityManager, bankPo.getPlayerId(), ItemPo.Type.BANK);
        items.forEach(bank::put);
        return bank;
    }

    @Override
    public Optional<Bank> find(long playerId) {
        try (var em = entityManagerFactory.createEntityManager()) {
            return findBankPo(em, playerId).map(bankPo -> convert(em, bankPo));
        }
    }
}

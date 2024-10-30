package org.y1000.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.*;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.persistence.*;
import org.y1000.sdb.ItemDrugSdb;

import java.util.*;
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


    private Set<Object> buildEquipmentAbilities(String name, int color) {
        Set<Object> abilities = new HashSet<>();
        if (itemSdb.isUpgrade(name)) {
            abilities.add(new UpgradableImpl());
        }
        if (itemSdb.isColoring(name)) {
            abilities.add(new DyableImpl(color));
        }
        return abilities;
    }

    public Equipment createEquipment(String name) {
        return createEquipment(name, itemSdb.getColor(name));
    }

    @Override
    public Equipment createEquipment(String name, int color) {
        Validate.notNull(name);
        Set<Object> abilities = buildEquipmentAbilities(name, color);
        EquipmentType equipmentType = itemSdb.getEquipmentType(name);
        return switch (equipmentType) {
            case WEAPON -> new WeaponImpl(name, itemSdb, abilities);
            case TROUSER, CLOTHING, HAIR -> new SexualEquipmentImpl(name, itemSdb, equipmentType, abilities);
            case HAT, CHEST, BOOT, WRIST, WRIST_CHESTED -> new ArmorImpl(name, itemSdb, abilities);
        };
    }

    @Override
    public Item createItem(GroundedItem item) {
        Validate.notNull(item, "item must not be null");
        Item restored = item.getNumber() != null ?
                createItem(item.getName(), item.getNumber()) : createItem(item.getName());
        if (restored instanceof Equipment equipment) {
            equipment.findAbility(Dyable.class).ifPresent(d -> d.dye(item.getColor()));
        }
        return restored;
    }


    private BuffPill createBuffPill(String name) {
        int last = itemDrugSdb.getStillInterval(name);
        int damage = itemDrugSdb.getDamageBody(name);
        return new BuffPill(name, itemSdb.getSoundDrop(name), itemSdb.getSoundEvent(name), itemSdb.getDesc(name), damage, last,
                itemSdb.getShape(name));
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
            case MONEY, SELLING_GOODS, GUILD_STONE -> new SimpleItem(name, type, itemSdb);
            case DYE -> new Dye(name, itemSdb);
            case PILL -> new Pill(name, new PillAttributeProviderImpl(name, itemSdb, itemDrugSdb));
            case KUNGFU -> createKungFuItem(name);
            case BANK_INVENTORY -> new BankInventory(name, itemSdb);
            case BUFF_PILL -> createBuffPill(name);
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
        return (SexualEquipment) createEquipment(name, itemSdb.getColor(name));
    }

    @Override
    public ArmorEquipment createHat(String name) {
        return (ArmorEquipment) createEquipment(name, itemSdb.getColor(name));
    }

    @Override
    public ArmorEquipment createChest(String name) {
        return (ArmorEquipment) createEquipment(name, itemSdb.getColor(name));
    }

    @Override
    public SexualEquipment createHair(String name) {
        return (SexualEquipment) createEquipment(name, itemSdb.getColor(name));
    }

    @Override
    public ArmorEquipment createBoot(String name) {
        return (ArmorEquipment) createEquipment(name, itemSdb.getColor(name));
    }

    @Override
    public ArmorEquipment createWrist(String name) {
        return (ArmorEquipment) createEquipment(name, itemSdb.getColor(name));
    }

    @Override
    public SexualEquipment createClothing(String name) {
        return (SexualEquipment) createEquipment(name, itemSdb.getColor(name));
    }

    private void deleteItems(EntityManager entityManager, long playerId, SlotKey.Type type) {
        entityManager.createQuery("delete from PlayerItemPo i where i.itemKey.playerId = ?1 and i.itemKey.type = ?2")
                .setParameter(1, playerId)
                .setParameter(2, type)
                .executeUpdate();
    }

    private Item createEquipment(PlayerItemPo playerItemPo) {
        var type = itemSdb.getTypeValue(playerItemPo.getName());
        if (ItemType.contains(type) && type == ItemType.EQUIPMENT.value()) {
            return createEquipment(playerItemPo.getName(), playerItemPo.getColor());
        } else {
            return createItem(playerItemPo.getName(), playerItemPo.getNumber());
        }
    }

    private Map<Integer, Item> findItems(EntityManager entityManager, long playerId, SlotKey.Type type) {
        return entityManager.createQuery("select i from PlayerItemPo i where i.itemKey.playerId = ?1 and i.itemKey.type = ?2", PlayerItemPo.class)
                .setParameter(1, playerId)
                .setParameter(2, type)
                .getResultStream()
                .collect(Collectors.toMap(i -> i.getItemKey().getSlot(), this::createEquipment));
    }

    @Override
    public Equipment createEquipment(EquipmentPo equipmentPo) {
        var e = createEquipment(equipmentPo.getName(), equipmentPo.getColor());
        e.setId(equipmentPo.getId());
        e.findAbility(Upgradable.class).ifPresent(u -> {
            while (u.level() < equipmentPo.getLevel())
                u.upgrade();
        });
        return e;
    }

    private Inventory createEquipment(EntityManager entityManager, InventoryPo inventoryPo) {
        Set<Long> ids = inventoryPo.selectEquipmentIds();
        Inventory inventory = new Inventory();
        Map<Long, Equipment> equipments = Collections.emptyMap();
        if (!ids.isEmpty()) {
            equipments = entityManager.createQuery("select e from EquipmentPo e where e.id in ?1", EquipmentPo.class)
                    .setParameter(1, ids)
                    .getResultStream()
                    .collect(Collectors.toMap(EquipmentPo::getId, this::createEquipment));
        }
        for (SlotItem slotItem : inventoryPo.getSlots()) {
            if (slotItem.isEquipment()) {
                inventory.put(slotItem.getSlot(), equipments.get(slotItem.getEquipmentId()));
            } else {
                inventory.put(slotItem.getSlot(), createItem(slotItem.getName(), slotItem.getNumber()));
            }
        }
        return inventory;
    }

    @Override
    public Optional<Inventory> findInventory(EntityManager entityManager, long playerId) {
        Validate.notNull(entityManager);
        Optional<InventoryPo> queryResult = entityManager.createQuery("select i from InventoryPo i where i.playerId = ?1", InventoryPo.class)
                .setParameter(1, playerId)
                .getResultStream()
                .findFirst();
        return queryResult.map(i -> createEquipment(entityManager, i));
    }

    private void persistEquipments(EntityManager entityManager, Inventory inventory) {
        List<Equipment> toInsert = new ArrayList<>();
        Map<Long, Equipment> toUpdate = new HashMap<>();
        inventory.foreach((slot, item) -> {
            if (item instanceof Equipment equipment) {
                if (equipment.id() == null)
                    toInsert.add(equipment);
                else
                    toUpdate.put(equipment.id(), equipment);
            }
        });
        for (Equipment equipment : toInsert) {
            EquipmentPo converted = EquipmentPo.convert(equipment);
            entityManager.persist(converted);
            equipment.setId(converted.getId());
        }
        if (toUpdate.isEmpty())
            return;
        List<EquipmentPo> equipmentPos = entityManager.createQuery("select e from EquipmentPo e where e.id in ?1", EquipmentPo.class)
                .setParameter(1, toUpdate.keySet())
                .getResultList();
        for (EquipmentPo equipmentPo : equipmentPos) {
            Equipment equipment = toUpdate.get(equipmentPo.getId());
            equipmentPo.merge(equipment);
        }
    }

    @Override
    public void saveInventory(EntityManager entityManager, long playerId, Inventory inventory) {
        Validate.notNull(entityManager);
        Validate.notNull(inventory);
        persistEquipments(entityManager, inventory);
        Optional<InventoryPo> queryResult = entityManager.createQuery("select i from InventoryPo i where i.playerId = ?1", InventoryPo.class)
                .setParameter(1, playerId)
                .getResultStream().findFirst();
        queryResult.ifPresentOrElse(inventoryPo -> inventoryPo.merge(inventory),
                () -> entityManager.persist(InventoryPo.convert(playerId, inventory)));
    }

    private void merge(EntityManager entityManager, BankPo bankPo, Bank bank) {
        bankPo.setUnlocked(bank.getUnlocked());
        bankPo.setCapacity(bank.capacity());
        bank.foreach((slot, item) -> entityManager.persist(PlayerItemPo.toBankItem(bankPo.getPlayerId(), slot, item)));
    }

    private void persist(EntityManager entityManager, long playerId, Bank bank) {
        BankPo bankPo = new BankPo(null, playerId, bank.capacity(), bank.getUnlocked());
        entityManager.persist(bankPo);
        bank.foreach((slot, item) -> entityManager.persist(PlayerItemPo.toBankItem(bankPo.getPlayerId(), slot, item)));
    }

    @Override
    public void save(long playerId, Bank bank) {
        Validate.notNull(bank);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            tx.begin();
            deleteItems(entityManager, playerId, SlotKey.Type.BANK);
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
        Map<Integer, Item> items = findItems(entityManager, bankPo.getPlayerId(), SlotKey.Type.BANK);
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

package org.y1000.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.y1000.entities.players.*;
import org.y1000.entities.players.PlayerFactory;
import org.y1000.kungfu.*;
import org.y1000.item.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.persistence.EquipmentPo;
import org.y1000.persistence.PlayerPo;
import org.y1000.util.Coordinate;

import java.util.*;

public final class PlayerRepositoryImpl implements PlayerRepository, PlayerFactory {

    private final ItemFactory itemFactory;
    private final KungFuBookFactory kungFuBookFactory;
    private final KungFuBookRepository kungFuRepository;

    private static final int DEFAULT_REALM_ID = 6;
    //private static final Coordinate DEFAULT_COORDINATE = Coordinate.xy(177, 88);
    private static final Coordinate DEFAULT_COORDINATE = Coordinate.xy(177, 221);

    private final EntityManagerFactory entityManagerFactory;

    private final ItemRepository itemRepository;

    private final GuildRepository guildRepository;

    public PlayerRepositoryImpl(ItemFactory itemFactory,
                                KungFuBookFactory kungFuBookFactory,
                                KungFuBookRepository kungFuRepository,
                                EntityManagerFactory entityManagerFactory,
                                ItemRepository itemRepository,
                                GuildRepository guildRepository) {
        this.itemFactory = itemFactory;
        this.kungFuBookFactory = kungFuBookFactory;
        this.kungFuRepository = kungFuRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.itemRepository = itemRepository;
        this.guildRepository = guildRepository;
    }


    /*
       AttribData.cEnergy   := GetLevel (AttribData.Energy) + 500;     // 기본원기 = 5.00
   AttribData.cInPower  := GetLevel (AttribData.InPower) + 1000;   // 기본내공 = 10.00
   AttribData.cOutPower := GetLevel (AttribData.OutPower) + 1000;  // 기본외공 = 10.00
   AttribData.cMagic    := GetLevel (AttribData.Magic) + 500;      // 기본무공 = 5.00
   AttribData.cLife     := GetLevel (AttribData.Life) + 2000;      // 기본활력 = 20.00
     */

    private static final PlayerInnateAttributesProvider DEV_PROVIDER = new PlayerInnateAttributesProvider() {
        @Override
        public Damage damage() {
            return new Damage(5000, 0, 0, 0);
        }
    };


    private Map<EquipmentType, Equipment> restoreEquipments(PlayerPo playerPo) {
        Map<EquipmentType, Equipment> equipments = new HashMap<>();
        for (EquipmentPo e: playerPo.getEquipments()) {
            Equipment equipment = itemFactory.createEquipment(e);
            equipments.put(equipment.equipmentType(), equipment);
        }
        return equipments;
    }

    private KungFuBook restoreKungFuBook(EntityManager entityManager, PlayerPo playerPo) {
        return kungFuRepository.find(entityManager, playerPo.getId())
                .orElseGet(kungFuBookFactory::create);
    }


    private Player restore(EntityManager entityManager, PlayerPo playerPo) {
        PlayerDefaultAttributes innate = PlayerDefaultAttributes.INSTANCE;
        Map<EquipmentType, Equipment> equipments = restoreEquipments(playerPo);
        KungFuBook kungFuBook = restoreKungFuBook(entityManager, playerPo);
        Equipment equipment = equipments.get(EquipmentType.WEAPON);
        var attackKUngFu = kungFuBook.findUnnamedAttack(AttackKungFuType.Fist);
        if (equipment instanceof Weapon weapon) {
            attackKUngFu = kungFuBook.findUnnamedAttack(weapon.kungFuType());
        }
        return PlayerImpl.builder()
                .id(playerPo.getId())
                .coordinate(playerPo.coordinate())
                .male(playerPo.isMale())
                .name(playerPo.getName())
                .yinYang(playerPo.yinYang())
                .life(playerPo.life(innate.life()))
                .head(playerPo.head(innate.life()))
                .leg(playerPo.leg(innate.life()))
                .arm(playerPo.arm(innate.life()))
                .power(playerPo.power(innate.power()))
                .innerPower(playerPo.innerPower(innate.innerPower()))
                .outerPower(playerPo.outerPower(innate.outerPower()))
                .revival(playerPo.getRevivalExp())
                .innateAttributesProvider(innate)
                .pillSlots(new PillSlots())
                .guildMembership(guildRepository.findGuildMembership(entityManager, playerPo.getId()).orElse(null))
                .inventory(itemRepository.findInventory(entityManager, playerPo.getId()).orElseGet(Inventory::new))
                .equipments(equipments)
                .kungFuBook(kungFuBook)
                .attackKungFu(attackKUngFu)
                .build();
    }

    @Override
    public Optional<Pair<Player, Integer>> find(int accountId, String charName) {
        Validate.notNull(charName);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()){
            return entityManager.createQuery("select p from PlayerPo p where p.accountId = ?1 and p.name = ?2", PlayerPo.class)
                    .setParameter(1, accountId).setParameter(2, charName)
                    .getResultStream()
                    .findFirst()
                    .map(playerPo -> new ImmutablePair<>(restore(entityManager, playerPo), playerPo.getRealmId()));
        }
    }



    @Override
    public Optional<Integer> findRealm(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("select p from PlayerPo p where p.id = ?1", PlayerPo.class)
                    .setParameter(1, id)
                    .getResultStream()
                    .findFirst()
                    .map(PlayerPo::getRealmId);
        }
    }


    @Override
    public void update(Player player) {
        Validate.notNull(player);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()){
            EntityTransaction tx = entityManager.getTransaction();
            tx.begin();
            PlayerPo playerPo = entityManager.find(PlayerPo.class, player.id());
            if (playerPo == null) {
                tx.rollback();
                return;
            }
            itemRepository.saveEquipments(entityManager, player.getEquipments());
            playerPo.merge(player);
            kungFuRepository.save(entityManager, playerPo.getId(), player.kungFuBook());
            itemRepository.saveInventory(entityManager, player.id(), player.inventory());
            tx.commit();
            setEquipmentIds(playerPo, player);
        }
    }

    @Override
    public Optional<Player> load(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("select p from PlayerPo p where p.id = ?1", PlayerPo.class)
                    .setParameter(1, id)
                    .getResultStream()
                    .findFirst()
                    .map(playerPo -> restore(entityManager, playerPo));
        }
    }

    private void setEquipmentIdIfNull(Equipment equipment, PlayerPo playerPo) {
        if (equipment.id() != null)
            return;
        playerPo.findEquipment(equipment.name())
                .ifPresent(e -> equipment.setId(e.getId()));
    }

    private void setEquipmentIds(PlayerPo playerPo, Player player) {
        player.hat().ifPresent(e -> setEquipmentIdIfNull(e, playerPo));
        player.chest().ifPresent(e -> setEquipmentIdIfNull(e, playerPo));
        player.wrist().ifPresent(e -> setEquipmentIdIfNull(e, playerPo));
        player.hair().ifPresent(e -> setEquipmentIdIfNull(e, playerPo));
        player.boot().ifPresent(e -> setEquipmentIdIfNull(e, playerPo));
        player.trouser().ifPresent(e -> setEquipmentIdIfNull(e, playerPo));
        player.clothing().ifPresent(e -> setEquipmentIdIfNull(e, playerPo));
        player.weapon().ifPresent(e -> setEquipmentIdIfNull(e, playerPo));
    }

    @Override
    public long save(EntityManager entityManager, int accountId, Player player) {
        Validate.notNull(entityManager);
        Validate.notNull(player);
        PlayerPo converted = PlayerPo.convert(player, accountId, DEFAULT_REALM_ID);
        itemRepository.saveEquipments(entityManager, player.getEquipments());
        converted.addEquipments(player.getEquipments());
        entityManager.persist(converted);
        kungFuRepository.save(entityManager, converted.getId(), player.kungFuBook());
        itemRepository.saveInventory(entityManager, player.id(), player.inventory());
        player.guildMembership().ifPresent(gm -> guildRepository.upsertMembership(entityManager, player.id(), gm));
        return converted.getId();
    }

    @Override
    public int countByName(EntityManager entityManager, String name) {
        Validate.notNull(entityManager);
        Validate.notNull(name);
        Query query = entityManager.createQuery("select count(p) from PlayerPo p where name = ?1");
        query.setParameter(1, name);
        return ((Long)query.getSingleResult()).intValue();
    }

    @Override
    public int countByAccount(EntityManager entityManager, int accountId) {
        Validate.notNull(entityManager);
        Query query = entityManager.createQuery("select count(p) from PlayerPo p where accountId = ?1");
        query.setParameter(1, accountId);
        return ((Long)query.getSingleResult()).intValue();
    }

    @Override
    public Player create(String name, boolean male) {
        Validate.notNull(name);
        var yinyang = new YinYang();
        var kungfuBook = kungFuBookFactory.create();
        return PlayerImpl.builder()
                .id(0)
                .name(name)
                .coordinate(DEFAULT_COORDINATE)
                .kungFuBook(kungfuBook)
                .attackKungFu(kungfuBook.findUnnamedAttack(AttackKungFuType.Fist))
                .inventory(new Inventory())
                .male(male)
                .innateAttributesProvider(PlayerDefaultAttributes.INSTANCE)
                .yinYang(yinyang)
                .revival(0)
                .life(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .head(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .arm(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .leg(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .power(new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.power(), yinyang.age()))
                .innerPower(new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.innerPower(), yinyang.age()))
                .outerPower(new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.outerPower(), yinyang.age()))
                .pillSlots(new PillSlots())
                .build();
    }

    public Player create(String name, boolean male, long id) {
        Validate.notNull(name);
        var yinyang = new YinYang();
        var kungfuBook = kungFuBookFactory.create();
        return PlayerImpl.builder()
                .id(id)
                .name(name)
                .coordinate(DEFAULT_COORDINATE)
                .kungFuBook(kungfuBook)
                .attackKungFu(kungfuBook.findUnnamedAttack(AttackKungFuType.Fist))
                .inventory(new Inventory())
                .male(male)
                .innateAttributesProvider(PlayerDefaultAttributes.INSTANCE)
                .yinYang(yinyang)
                .revival(0)
                .life(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .head(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .arm(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .leg(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .power(new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.power(), yinyang.age()))
                .innerPower(new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.innerPower(), yinyang.age()))
                .outerPower(new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.outerPower(), yinyang.age()))
                .pillSlots(new PillSlots())
                .build();
    }
}

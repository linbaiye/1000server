package org.y1000.repository;

import org.y1000.kungfu.AssistantKungFu;
import org.y1000.kungfu.KungFuBookFactory;
import org.y1000.item.*;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.KungFuBook;
import org.y1000.util.Coordinate;

public final class PlayerRepositoryImpl implements PlayerRepository {

    private static final int[] slots = new int[]{-1, -1, 1,-1,-1,-1,-1,-1,-1,-1};

    private static final long playerIdStart = 1000000000;

    private final ItemFactory itemFactory;
    private final KungFuBookFactory kungFuBookFactory;
    private final KungFuBookRepository kungFuRepository;

    public PlayerRepositoryImpl(ItemFactory itemFactory,
                                KungFuBookFactory kungFuBookFactory,
                                KungFuBookRepository kungFuRepository) {
        this.itemFactory = itemFactory;
        this.kungFuBookFactory = kungFuBookFactory;
        this.kungFuRepository = kungFuRepository;
    }

    private int findSlot() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == -1) {
                slots[i] = 0;
                return i;
            }
        }
        return 0;
    }

    private Inventory loadInventory() {
        Inventory inventory = new Inventory();
        inventory.add(itemFactory.createItem("长剑"));
        inventory.add(itemFactory.createItem("木弓"));
        inventory.add(itemFactory.createItem("箭", 10000));
        inventory.add(itemFactory.createItem("太极斧"));
        inventory.add(itemFactory.createItem("太极神枪"));
        inventory.add(itemFactory.createItem("三叉戟"));
        inventory.add(itemFactory.createItem("黄金手套"));
        inventory.add(itemFactory.createItem("北海连环弓"));
        /*inventory.add(itemFactory.createItem("女子血魔道袍"));
        inventory.add(itemFactory.createItem("血化戟"));
        inventory.add(itemFactory.createItem("血皇斧"));*/
        return inventory;
    }

    private Weapon weapon() {
        return (Weapon) itemFactory.createItem("护国神剑");
        //return (Weapon) itemFactory.createItem("太极斧");
    }

    private KungFuBook loadKungFuBook() {
        KungFuBook kungFuBook = kungFuBookFactory.create();
        kungFuBook.addToBasic(AssistantKungFu.builder().name("风灵旋").level(100).eightDirection(false).build());
        kungFuBook.addToBasic(AssistantKungFu.builder().name("灵动八方").level(100).eightDirection(true).build());
        return kungFuBook;
    }


    @Override
    public Player load(String token) {
        int slot = findSlot();
        Weapon weapon = weapon();
        KungFuBook kungFuBook = loadKungFuBook();
        return PlayerImpl.builder()
                .id(slot + playerIdStart)
                .name("杨过")
                .coordinate(new Coordinate(39 + slot, 27))
                .weapon(weapon)
                .kungFuBook(kungFuBook)
                .attackKungFu(kungFuBook.findUnnamedAttack(weapon.kungFuType()))
                .footKungfu(kungFuBook.getUnnamedFoot())
                .protectKungFu(kungFuBook.getUnnamedProtection())
                .inventory(loadInventory())
                .male(false)
                .trouser(new Trouser("女子短裙"))
                .clothing(new Clothing("女子上衣"))
                //.boot(new Boot("女子长靴"))
                .boot(new Boot("女子皮鞋"))
                //.boot(new Boot("女子魔人战靴"))
                 .hat(new Hat("女子血魔头盔"))
               // .chest(new Chest("女子太极道袍"))
                // .chest(new Chest("女子黄金铠甲"))
                //.chest(new Chest("女子血魔道袍"))
                //.hat(new Hat("女子雨中客斗笠"))
                //.chest(new Chest("女子黄龙弓服"))
                .chest(new Chest("女子雨中客道袍"))
                .hair(new Hair("女子麻花辫"))
                .wrist(new Wrist("女子太极护腕"))
                .build();
    }

    @Override
    public void save(Player player) {
        slots[(int)player.id()] = -1;
    }

}

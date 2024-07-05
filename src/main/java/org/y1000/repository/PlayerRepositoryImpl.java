package org.y1000.repository;

import org.y1000.entities.players.*;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.kungfu.KungFuBookFactory;
import org.y1000.item.*;
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
        inventory.add(itemFactory.createItem("箭", 10000));
        inventory.add(itemFactory.createItem("太极斧"));
        inventory.add(itemFactory.createItem("太极神枪"));
        inventory.add(itemFactory.createItem("三叉戟"));
        inventory.add(itemFactory.createItem("黄金手套"));
        inventory.add(itemFactory.createItem("北海连环弓"));
        inventory.add(itemFactory.createItem(DefaultStackItem.MONEY, 10000));
        inventory.add(itemFactory.createItem("生药", 10000));
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
        kungFuBook.addToBasic(AssistantKungFu.builder().name("风灵旋").exp(0).eightDirection(false).build());
        kungFuBook.addToBasic(AssistantKungFu.builder().name("灵动八方").exp(0).eightDirection(true).build());
        return kungFuBook;
    }


    /*
       AttribData.cEnergy   := GetLevel (AttribData.Energy) + 500;     // 기본원기 = 5.00
   AttribData.cInPower  := GetLevel (AttribData.InPower) + 1000;   // 기본내공 = 10.00
   AttribData.cOutPower := GetLevel (AttribData.OutPower) + 1000;  // 기본외공 = 10.00
   AttribData.cMagic    := GetLevel (AttribData.Magic) + 500;      // 기본무공 = 5.00
   AttribData.cLife     := GetLevel (AttribData.Life) + 2000;      // 기본활력 = 20.00
     */

    @Override
    public Player load(String token) {
        int slot = findSlot();
        Weapon weapon = weapon();
        KungFuBook kungFuBook = loadKungFuBook();
        var yinyang = new YinYang();
        return PlayerImpl.builder()
                .id(slot + playerIdStart)
                .name("雨诗妾")
                .coordinate(new Coordinate(175+ slot, 40))
                .weapon(weapon)
                .kungFuBook(kungFuBook)
                .attackKungFu(kungFuBook.findUnnamedAttack(weapon.kungFuType()))
                .footKungfu(kungFuBook.getUnnamedFoot())
                .protectKungFu(kungFuBook.getUnnamedProtection())
                .inventory(loadInventory())
                .male(false)
                .trouser(itemFactory.createTrouser("女子短裙"))
                .clothing(itemFactory.createClothing("女子上衣"))
                .boot(itemFactory.createBoot("女子皮鞋"))
                .hat(itemFactory.createHat("女子血魔头盔"))
                .chest(itemFactory.createChest("女子雨中客道袍"))
                .hair(itemFactory.createHair("女子麻花辫"))
                .wrist(itemFactory.createWrist("女子太极护腕"))
                .innateAttributesProvider(PlayerDefaultAttributes.INSTANCE)
                .yinYang(yinyang)
                .revival(0)
                .life(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .head(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .arm(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .leg(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .power(new PlayerExperiencedAgedAttribute("武功", PlayerDefaultAttributes.INSTANCE.power(), yinyang.age()))
                .innerPower(new PlayerExperiencedAgedAttribute("内功", PlayerDefaultAttributes.INSTANCE.innerPower(), yinyang.age()))
                .outerPower(new PlayerExperiencedAgedAttribute("外功", PlayerDefaultAttributes.INSTANCE.outerPower(), yinyang.age()))
                .build();
    }

    @Override
    public void save(Player player) {
        slots[(int)player.id()] = -1;
    }

}

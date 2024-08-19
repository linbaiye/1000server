package org.y1000.repository;

import org.y1000.entities.players.*;
import org.y1000.exp.ExperienceUtil;
import org.y1000.kungfu.*;
import org.y1000.item.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
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
        inventory.add(itemFactory.createItem("箭", 10000));
        inventory.add(itemFactory.createItem("太极斧"));
        inventory.add(itemFactory.createItem("三叉戟"));
        inventory.add(itemFactory.createItem("黄金手套"));
        inventory.add(itemFactory.createItem("北海连环弓"));
        inventory.add(itemFactory.createMoney( 10000));
        inventory.add(itemFactory.createItem("金毛草", 10000));
        inventory.add(itemFactory.createItem("大脉神弓"));
        inventory.add(itemFactory.createItem("女子黄龙弓服"));
        inventory.add(itemFactory.createItem("女子黄龙手套"));
        inventory.add(itemFactory.createItem("女子黄龙鞋"));
        inventory.add(itemFactory.createItem("血魔剑"));
        inventory.add(itemFactory.createItem("血化戟"));
        inventory.add(itemFactory.createItem("骨钥匙", 1000));
        inventory.add(itemFactory.createItem("火石", 1000));
        inventory.add(itemFactory.createItem("追魂索", 10000));
        inventory.add(itemFactory.createItem("紫色染剂", 10));
        inventory.add(itemFactory.createItem("女子束长发"));
        inventory.add(itemFactory.createItem("红色染剂", 10));
        /*inventory.add(itemFactory.createItem("女子血魔道袍"));
        inventory.add(itemFactory.createItem("血化戟"));
        inventory.add(itemFactory.createItem("血皇斧"));*/
        return inventory;
    }

    private Weapon weapon() {
        return (Weapon) itemFactory.createItem("护国神剑");
        //return (Weapon) itemFactory.createItem("太极斧");
    }

    private void levelUp(KungFu kungFu) {
        while (kungFu.level() < 9999) {
            kungFu.gainPermittedExp(ExperienceUtil.MAX_EXP);
        }
    }

    private KungFuBook loadKungFuBook() {
        KungFuBook kungFuBook = kungFuBookFactory.create();
        AttackKungFu bow = kungFuBook.findUnnamedAttack(AttackKungFuType.BOW);
        levelUp(bow);
        kungFuBook.addToBasic(AssistantKungFu.builder().name("风灵旋").exp(0).eightDirection(false).build());
        AssistantKungFu ld = AssistantKungFu.builder().name("灵动八方").exp(ExperienceUtil.MAX_EXP).eightDirection(true).build();
        kungFuBook.addToBasic(ld);
        KungFu sword = ((KungFuFactory) kungFuBookFactory).create("壁射剑法");
        levelUp(sword);
        kungFuBook.addToBasic(sword);

        sword = ((KungFuFactory) kungFuBookFactory).create("点枪术");
        levelUp(sword);
        kungFuBook.addToBasic(sword);

        sword = ((KungFuFactory) kungFuBookFactory).create("闪光剑破解");
        levelUp(sword);
        kungFuBook.addToBasic(sword);
        var prot = ((KungFuFactory) kungFuBookFactory).create("金钟罩");
        levelUp(prot);
        kungFuBook.addToBasic(prot);
        var bufa = ((KungFuFactory) kungFuBookFactory).create("幻魔身法");
        levelUp(bufa);
        kungFuBook.addToBasic(bufa);
        return kungFuBook;
    }


    /*
       AttribData.cEnergy   := GetLevel (AttribData.Energy) + 500;     // 기본원기 = 5.00
   AttribData.cInPower  := GetLevel (AttribData.InPower) + 1000;   // 기본내공 = 10.00
   AttribData.cOutPower := GetLevel (AttribData.OutPower) + 1000;  // 기본외공 = 10.00
   AttribData.cMagic    := GetLevel (AttribData.Magic) + 500;      // 기본무공 = 5.00
   AttribData.cLife     := GetLevel (AttribData.Life) + 2000;      // 기본활력 = 20.00
     */

    public static int lastRealmId() {
        return 49;
    }

    private static final PlayerInnateAttributesProvider DEV_PROVIDER = new PlayerInnateAttributesProvider() {
        @Override
        public Damage damage() {
            return new Damage(5000, 0, 0, 0);
        }
    };
    private PlayerImpl createFemale() {
        int slot = findSlot();
        Weapon weapon = weapon();
        KungFuBook kungFuBook = loadKungFuBook();
        var yinyang = new YinYang();
        return PlayerImpl.builder()
                .id(slot + playerIdStart)
                .name("雨诗妾")
              //   .coordinate(new Coordinate(33, 167)) // 王陵1层 -> 2入口
               // .coordinate(new Coordinate(500, 500))
               //.coordinate(new Coordinate(516, 478))
               // .coordinate(new Coordinate(55, 51)) // 王陵2层
                //.coordinate(new Coordinate(80, 157)) // 王陵2层
               // .coordinate(new Coordinate(32, 165)) // 王陵2层
                //.coordinate(new Coordinate(37, 49)) //修炼洞
                .coordinate(new Coordinate(94, 59)) //新手村
                .weapon(weapon)
                .kungFuBook(kungFuBook)
                .attackKungFu(kungFuBook.findUnnamedAttack(weapon.kungFuType()))
                .inventory(loadInventory())
                .male(false)
                .trouser(itemFactory.createTrouser("女子短裙"))
                //.clothing(itemFactory.createClothing("女子上衣"))
                .boot(itemFactory.createBoot("女子血魔战靴"))
                //.boot(itemFactory.createBoot("女子皮鞋"))
                .hat(itemFactory.createHat("女子血魔头盔"))
                //.hat(itemFactory.createHat("女子百炼雨中客斗笠"))
                .chest(itemFactory.createChest("女子血魔道袍"))
                //.chest(itemFactory.createChest("女子百炼雨中客道袍"))
                .hair(itemFactory.createHair("女子束长发"))
                //.hair(itemFactory.createHair("女子麻花辫"))
                .wrist(itemFactory.createWrist("女子血魔护腕"))
                //.wrist(itemFactory.createWrist("女子太极护腕"))
                .innateAttributesProvider(DEV_PROVIDER)
                //.innateAttributesProvider(PlayerDefaultAttributes.INSTANCE)
                .yinYang(yinyang)
                .revival(0)
                .life(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .head(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .arm(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .leg(new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), yinyang.age()))
                .power(new PlayerExperiencedAgedAttribute("武功", PlayerDefaultAttributes.INSTANCE.power(), yinyang.age()))
                .innerPower(new PlayerExperiencedAgedAttribute("内功", PlayerDefaultAttributes.INSTANCE.innerPower(), yinyang.age()))
                .outerPower(new PlayerExperiencedAgedAttribute("外功", PlayerDefaultAttributes.INSTANCE.outerPower(), yinyang.age()))
                .pillSlots(new PillSlots())
                .build();
    }

    private PlayerImpl createMale() {
        int slot = findSlot();
        Weapon weapon = weapon();
        KungFuBook kungFuBook = loadKungFuBook();
        var yinyang = new YinYang();
        return PlayerImpl.builder()
                .id(slot + playerIdStart)
                .name("拓跋")
                //.coordinate(new Coordinate(175+ ropeSlot, 40))
                .coordinate(new Coordinate(500, 500))
                //.coordinate(new Coordinate(309, 148))
                //.coordinate(new Coordinate(129, 99))
                //.coordinate(new Coordinate(38, 50)) //修炼洞
                //.coordinate(new Coordinate(98, 46)) //新手村
                // .coordinate(new Coordinate(104, 60))
                .weapon(weapon)
                .kungFuBook(kungFuBook)
                .attackKungFu(kungFuBook.findUnnamedAttack(weapon.kungFuType()))
                .footKungfu(kungFuBook.getUnnamedFoot())
                .protectKungFu(kungFuBook.getUnnamedProtection())
                .inventory(loadInventory())
                .male(true)
                .hat(itemFactory.createHat("男子雨中客斗笠"))
                .chest(itemFactory.createChest("男子雨中客道袍"))
                .wrist(itemFactory.createWrist("男子黄龙手套"))
                .boot(itemFactory.createBoot("男子利齿靴"))
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
                .pillSlots(new PillSlots())
                .build();
    }

    @Override
    public Player load(String token) {
        return createFemale();
        //return createMale();
    }

    @Override
    public void save(Player player) {
        slots[(int)player.id()] = -1;
    }

}

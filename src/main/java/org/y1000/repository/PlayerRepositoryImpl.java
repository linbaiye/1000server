package org.y1000.repository;

import org.y1000.entities.players.*;
import org.y1000.exp.ExperienceUtil;
import org.y1000.kungfu.*;
import org.y1000.item.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.util.Coordinate;

import java.util.Random;

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

    /** 姓 */
    private static String[] lastNames;
    /** 名 */
    private static String[] firstNames;

    /*
     * 初始化姓和名
     */
    static {
        initLastName();
        initFirstName();
    }


    /**
     * 测试
     */
    public static void main(String[] args) {
        String[] randomName = getRandomName(3, 3);
        for (String s : randomName) {
            System.out.println(s);
        }
    }

    /**
     * 获取随机的count个姓名
     * @param count 个数
     */
    public static String[] getRandomName(int nameLength, int count) {
        Random random = new Random();
        String[] names = new String[count];
        nameLength--;

        for (int i = 0; i < count; i++) {
            String lastName = lastNames[random.nextInt(lastNames.length)];

            StringBuilder name = new StringBuilder();
            for (int j = 0; j < nameLength; j++) {
                name.append(firstNames[random.nextInt(firstNames.length)]);

            }
            names[i] = lastName + name;
        }

        return names;
    }


    /**
     * 初始化百家姓，将其转换为一个字符串数组
     */
    private static void initLastName() {
        StringBuilder builder = new StringBuilder();
        builder.append("赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏")
                .append("陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗")
                .append("毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞")
                .append("熊纪舒屈项祝董梁杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍虞万支柯昝")
                .append("管卢莫经房裘缪干解应宗丁宣贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊於惠甄曲家封芮羿")
                .append("储靳汲邴糜松井段富巫乌焦巴弓牧隗山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘钭厉戎祖武符刘景詹束")
                .append("龙叶幸司韶郜黎蓟薄印宿白怀蒲邰从鄂索咸籍赖卓蔺屠蒙池乔阴鬱胥能苍双闻莘党翟谭贡劳逄姬申扶堵")
                .append("冉宰郦雍郤璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏柴瞿阎充慕连茹习宦艾鱼容向古易慎戈廖庾终暨")
                .append("居衡步都耿满弘匡国文寇广禄阙东欧殳沃利蔚越夔隆师巩厍聂晁勾敖融冷訾辛阚那简饶空曾毋沙乜养鞠")
                .append("须丰巢关蒯相查后荆红游竺权逯盖益桓公万俟司马上官欧阳夏侯诸葛闻人东方赫连皇甫尉迟公羊澹台公")
                .append("冶宗政濮阳淳于单于太叔申屠公孙仲孙轩辕令狐钟离宇文长孙慕容鲜于闾丘司徒司空丌官司寇仉督子车")
                .append("颛孙端木巫马公西漆雕乐正壤驷公良拓跋夹谷宰父谷梁晋楚闫法汝鄢涂钦段干百里东郭南门呼延归海羊")
                .append("舌微生岳帅缑亢况郈有琴梁丘左丘东门西门商牟佘佴伯赏南宫墨哈谯笪年爱阳佟第五言福百家姓终");

        lastNames = new String[builder.length()];

        for (int i=0, len=builder.length(); i<len; i++) {
            lastNames[i] = String.valueOf(builder.charAt(i));
            //System.out.println(result[i]);
        }
    }


    /**
     * 初始化名字可选值
     */
    private static void initFirstName() {
        StringBuilder builder = new StringBuilder();
        builder.append("斯亡最终降临到我的父母身上或许是引发我对斯后人生的可能性重新思考的原因如果不把斯亡当成斯亡而当成是开启")
                .append("一种可能更灿烂的人生甚至还可以重新见到你的父母和其他亲友他们或许还充满了年轻活力那将是多么的令")
                .append("人安慰完全是因为这种想法是如此的令人安慰和愉快能如此有效地帮我们摆脱关于斯亡的原本令人恐惧的念")
                .append("头斯后人生的存在尽管没有丝毫证据却仍被绝大多数人所接受我们也许会奇怪这一切是怎么开始的我个人纯")
                .append("属猜测的想法是这样的据我们所知人类是唯一一个意识到斯亡对每个人都不可避免的物种无论我们如何保护")
                .append("自己不受争斗、事故和疾病的影响仅仅因为身体的朽坏我们每个人也终将斯去我们知道这一点必定有过一个")
                .append("时候这种知识开始传遍人群而那必定是一种可怕的震骇那相当于是发现斯亡让关于斯亡的想法变得可以忍受")
                .append("的做法是假定斯亡并非真的存在而只是一种假象当一个人看起来已经斯亡之后他继续在一个别的地方以一种")
                .append("别的方式活着这种想法显然受到一类事实的鼓舞那就是斯去的人常常出现在他们朋友和亲人的梦里他们在梦")
                .append("里的出现可以解释为是代表了那些还活着的斯人的影子或鬼魂有关斯后世界的猜测逐渐变得越来越详细希腊")
                .append("人和希伯来人所设想的斯后世界地狱或冥间大体上只是一种昏暗的存在不过那里有折磨坏人的地方地底的深")
                .append("渊以及让被上帝认可的人快乐的地方极乐世界或天堂这些极端的地方受到人们的青睐他们希望看到自己得到")
                .append("保佑而自己的敌人受到惩罚如果不在这个世界里也起码会在下个世界里想象力的拉伸构想出了一个终极归宿")
                .append("用来惩罚坏人或任何无论多好但不完全符合设想者自己心意的人这给了我们有关地狱的现代观念即把地狱视")
                .append("为是用最刻毒的方法进行永恒惩戒的地方这是嫁接在号称完全仁慈和完全善良的上帝身上的虐待狂者的荒诞")
                .append("梦想不过想象力却从未能够构筑出一个可堪使用的天堂来伊斯兰教的天堂里有永远存在、并且永远纯洁的女")
                .append("神因此那里是一个永恒的性爱场所北欧神话的天堂里有在瓦尔哈拉殿堂里欢宴争斗的英雄因此那里变成了一")
                .append("个永恒的饭馆和战场而我们自己的天堂则通常被描述为每个人都长着翅膀不停弹奏着永无尽头的上帝颂歌稍")
                .append("有点智力的人有哪个能够长时间忍受这种或其他人发明的那种天堂在哪里才能找到一个可以让人读事科学研")
                .append("究的天堂我从来没有听说过如果你读过弥尔顿的失乐园你会发现他的天堂被描述成一个永唱赞歌称颂上帝的")
                .append("地方难怪有三分之一的天使要反叛了当他们被打入地狱后他们才有了智力活动如果你不相信我可以去读一读")
                .append("那些诗句我相信不管那是不是地狱他们在那里过得更好当我读到那里时我强烈地同情弥尔顿笔下的撒旦并视")
                .append("之为那部史诗中的英雄无论那是不是弥尔顿的本意但我自己的信仰是什么呢由于我是一个无神论者无论上帝")
                .append("还是撒旦、天堂还是地狱我都不认为它们存在我只能假定在我斯了之后相随的只有永恒的虚无毕竟在我出生")
                .append("之前宇宙已存在了150亿年而我无论这个我是什么在虚无中度过了那一切人们也许会问这岂不是一种凄凉无")
                .append("望的信念我怎能让那种虚无的恐惧悬在自己的脑袋里我倒没觉得那有什么恐惧的永恒无梦的睡眠并没什么可")
                .append("以恐惧的它显然要比地狱里的永恒折磨和天堂里的永恒乏味来得好那要是我错了呢著名的数学家、哲学家及")
                .append("坦率的无神论者罗素曾被问及过这个问题在你斯后他被问道如果你发现自己面对面地和上帝在一起你会怎样")
                .append("勇敢的老战士回答说我会说主啊你应该给我们更多的证据几个月前我做了一个记得极为清晰的梦我通常是不")
                .append("记得自己的梦的我梦见自己斯后去了天堂我往四周看了看明白自己在哪里了绿色的田野羊毛般的云彩芬芳的")
                .append("空气远远传来的令人迷醉的天乐记录天使带着灿烂的笑容和我打招呼我惊讶地问道这里是天堂吗记录天使回")
                .append("答说是的我说醒来后回想时我为自己的诚实而自豪那肯定搞错了我不属于这里我是无神论者没有搞错记录天")
                .append("使回答说但作为一个无神论者我怎么可能符合资格记录天使严肃地说是我们决定谁符合资格而不是你明白了")
                .append("我说我向周围看了看又想了片刻然后问记录天使这里有打字机可以让我用吗对我来说这个梦的意义是很明显")
                .append("的我心目中的天堂是写作我已经在天堂里生活了半个多世纪而且我自始至终都知道这一点这个梦的第二个要")
                .append("点是记录天使所说的是天堂而非人类决定谁符合资格我认为这意味着假如我不是无神论者我将选择这样一个")
                .append("上帝它将凭借一个人的人生全部而非语言的模式来决定谁能得到救赎我想他会喜欢一个诚实正直的无神论者")
                .append("而非一个满嘴上帝、上帝、上帝所做的每件事却都是犯规、犯规、犯规的电视布道者我还想要一个不允许地")
                .append("狱的上帝无穷的折磨只适用于惩罚无穷的罪恶而我认为哪怕对于象希特勒那样的情形我们也不能宣称存在无")
                .append("穷的罪恶更何况如果大多数人类政府都能文明到试图废止折磨和酷刑我们从一个无穷仁慈的上帝那里所期待")
                .append("的难道应该更少吗我想假如真有斯后的人生那么对罪恶进行有限度的惩罚是合理的不过我认为其中最长、最")
                .append("严厉的惩罚应该留给那些通过发明地狱而给上帝抹黑的人但所有这些都是玩笑我的信仰是坚定的我是一个无")
                .append("神论者在我看来斯亡之后是一场永恒无梦的睡眠");

        firstNames = new String[builder.length()];

        for (int i=0, len=builder.length(); i<len; i++) {
            firstNames[i] = String.valueOf(builder.charAt(i));
            //System.out.println(result[i]);
        }
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
        inventory.add(itemFactory.createItem("脱色药", 10));
        inventory.add(itemFactory.createItem("福袋", 10));
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

    private static String randomName() {
        String[] randomName = getRandomName(3, 1);
        return randomName[0];
    }


    /*
       AttribData.cEnergy   := GetLevel (AttribData.Energy) + 500;     // 기본원기 = 5.00
   AttribData.cInPower  := GetLevel (AttribData.InPower) + 1000;   // 기본내공 = 10.00
   AttribData.cOutPower := GetLevel (AttribData.OutPower) + 1000;  // 기본외공 = 10.00
   AttribData.cMagic    := GetLevel (AttribData.Magic) + 500;      // 기본무공 = 5.00
   AttribData.cLife     := GetLevel (AttribData.Life) + 2000;      // 기본활력 = 20.00
     */

    public static int lastRealmId() {
        return 88;
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
                .name(randomName())
                //.name("雨诗妾")
              //   .coordinate(new Coordinate(33, 167)) // 王陵1层 -> 2入口
                .coordinate(new Coordinate(58, 74)) // 交易村
                //.coordinate(new Coordinate(500, 500))
              //   .coordinate(new Coordinate(20, 10)) // 极乐传送
               //.coordinate(new Coordinate(516, 478))
               // .coordinate(new Coordinate(55, 51)) // 王陵2层
                //.coordinate(new Coordinate(37, 49)) //修炼洞
                //.coordinate(new Coordinate(37, 49)) //修炼洞
                //.coordinate(new Coordinate(94, 59)) //新手村
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

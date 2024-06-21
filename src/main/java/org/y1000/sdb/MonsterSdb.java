package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;
import org.y1000.kungfu.KungFuSdb;

import java.util.Set;

public final class MonsterSdb extends AbstractSdbReader {

    public static final MonsterSdb INSTANCE = new MonsterSdb();

    private MonsterSdb() {
        read("Monster.sdb");
    }

    public int getRecovery(String name) {
        return getInt(name, "Recovery");
    }


    public String getAnimate(String name) {
        return get(name, "Animate");
    }

    public int getAvoid(String name) {
        return getIntOrZero(name, "Avoid");
    }

    public int getAttackSpeed(String name) {
        return getInt(name, "AttackSpeed");
    }

    public String getSoundAttack(String name) {
        return get(name, "SoundAttack");
    }

    public String getSoundStructed(String name) {
        return get(name, "SoundStructed");
    }

    public String getSoundStart(String name) {
        return get(name, "SoundStart");
    }

    public String getSoundNormal(String name) {
        return get(name, "SoundNormal");
    }

    public String getSoundDie(String name) {
        return get(name, "SoundDie");
    }


    public int getLife(String name) {
        return getInt(name, "Life");
    }

    public int getAccuracy(String name) {
        return getIntOrZero(name, "Accuracy");
    }

    public int getDamage(String name) {
        return getIntOrZero(name, "Damage");
    }

    public int getArmor(String name) {
        return getIntOrZero(name, "Armor");
    }

    public int getActionWidth(String name) {
        return getInt(name, "ActionWidth");
    }

    public String getAttackMagic(String name) {
        return get(name, "AttackMagic");
    }

    public boolean isPassive(String name) {
        String s = get(name, "boGoodHeart");
        return "TRUE".equals(s);
    }

    public static void main(String[] args) {
        MonsterSdb monsterSdb = INSTANCE;
        Set<String> strings = monsterSdb.names();
        for (String string : strings) {
            String s = monsterSdb.get(string, "AttackMagic");
            if (!StringUtils.isEmpty(s)) {
                System.out.print(string + ": " + s + "  ");
                System.out.println(KungFuSdb.INSTANCE.get(s.split(":")[0], "BowImage"));
            }
        }
        for(String header: KungFuSdb.INSTANCE.columnNames()) {
            String name = KungFuSdb.INSTANCE.get("标枪法", header);
            if (StringUtils.isNotEmpty(name)) {
                System.out.println(header + ": " + name);
            }
        }
    }
}

    /*
    Name,                怪物名称；
ViewName,        所显示的名称；
Kind,                种类、性质；除了“九尾狐变身”是“4”之外，其他都为空，相信这个设置是诱发某些事件的
Virtue,                浩然正气；杀死该怪物后获得的浩然正气值
ExtraExp,        额外、特别的经验；作用不详，
VirtueLevel,        浩然正气等级；指该怪所能修炼浩然正气的上限
BoOnlyOnce,        是否唯一一次；作用不详，默认=空
RegenInterval,        重生间隔；单位：毫秒，[注意]：这不是刷怪时间，而是该怪死后复活需要的时间。
CallInterval,        呼叫间隔；作用不详，默认=空
HideInterval,        隐藏间隔；作用不详，默认=空
BoSeller,        是不售货员；这是NPC的设置，怎么跑来这里啦，奇怪了
SpellResistRate,符咒抵抗等级；作用不详
ActionWidth,        活动范围；
Animate,        #译意=生气勃勃；
Shape,                外型；客户端显示图片
WalkSpeed,        移动速度；数值大的移动慢
Damage,                身体破坏；
DamageHead,        头部破坏；
DamageArm,        手部破坏；
DamageLeg,        脚部破坏；
Armor,                身体防御；
Life,                生命值；
AttackSpeed,        攻击速度；
Avoid,                躲闪；
Recovery,        恢复；
Accuracy,        维持；
SpendLife,        消耗活力；因该受到的攻击如果不超过自身防御的话，所掉的活力，默认“10”
HitArmor,        打击防御；作用不详，默认=空
BoViewHuman,        是否能看见玩家；
BoAutoAttack,        是否自动攻击；
BoGoodHeart,        是否好心的；
BoHit,                是否受到攻击；默认=“TRUE”，“太极公子”=“空”不受攻击，需要特定条件才能受到攻击。
BoNotBowHit,        是否不受远程攻击；
BoIce,                是否冻结；应该是在特定情况下该怪会停止移动/攻击，如[水石场2]中的“石巨人”
BoControl,        是否被控制；默认=空，只有“石头”=“TRUE”
BoRightRemove,        能否正确的移动；
EscapeLife,        开始逃亡的活力值；“蹄影”就设置高点吧，哈哈。
ViewWidth,        视野范围；
BoAttack,        是否攻击；
BoBoss,                是否为[BOSS]；
BoVassal,        是否为联手攻击；
VassalCount,        联手攻击时的数量；
AttackType,        攻击类型；
AttackMagic,        攻击时候使用的武功；格式为“四臂金刚术:10000”
HaveMagic,        所拥有的能力；格式“透视”或“变身术:医病术:搜集术”
BoChangeTarget,        是否改变攻击目标；这里的意思是，怪贴近玩家身边时，如果受到其他人攻击，会改变目标。
SoundStart,        刷新时候的声音；
SoundAttack,        发起攻击时的声音；
SoundDie,        死亡时的声音；
SoundNormal,        平常时的声音；
SoundStructed,
EffectStart,        开始出来的效果；作用不详，默认=空
EffectStructed,        受到攻击时的效果；作用不详，默认=空
EffectEnd,        结束效果；        作用不详，默认=空
FallItem,        掉落的物品；格式：“金元:1:2:银元:10:16”，
FallItemRandomCount,掉落物品的几率；默认=“1”就是说必掉“FallItem”的物品
HaveItem,        拥有的物品；我觉得和“FallItem”用途重复，格式：“肉:2:1:鹿角一:1:4:鹿茸一:1:10”
XControl,YControl,似乎只有“石头”才有这两选项，都是“1”
BoRandom,        是否随机；作用不详，有待补充
BoPK,                是否PK；可能是见什么打什么的，作用不详有待补充
ArmorWHPercent,        防御率；此属性是受到攻击后所抵消的攻击力，以百分比计算，如果设置成“100”哪是打不掉血
ShortExp,        近距离普通武功获得的经验值；
LongExp,        远距离普通武功获得的经验值；
RiseShortExp,        近距离2层武功获得的经验值；
RiseLongExp,        远距离2层武功获得的经验值；
HandExp,        掌风获得的经验值；
BestShortExp,        绝世武功1级获得的经验值；
BestShortExp2,        绝世武功2级获得的经验值；
BestShortExp3,        绝世武功3级获得的经验值；
3HitExp,        [真气]获得的经验值；
LimitSkill,        修炼武功的上限；修改稻草人的50级上限在这里改
=============[要改人型怪就在下面设置]==========
MonType,        是否人型怪；“1”=人型怪，不填=一般怪
sex,                性别；默认是女的，留空或“NULL”=女，“TRUE”=男
arr_body,        身体；默认=空=正常显示，填进其他字符，怪物边隐形，若其他部位有装备的话，只能看到装备
arr_gloves,        手套；“女子黄龙手套”
arr_upunderwear,上衣；“女子上衣”
arr_shoes,        鞋子；“女子黄龙鞋”
arr_downunderwear,裤子；“女子短裤”
arr_upoverwear,        外套；“女子黄龙弓服”
arr_hair,        头发；“女子束长发：5”
arr_cap,        帽子；“女子斗笠”
arr_weapon,        武器；“银狼破皇剑”
GUIld,                所属门派；“帝王陵章”
GroupKey,        团队；“1234”
FirstDir,        #作用不详；
QuestNum,        #作用不详；
QuestHaveItem,        #作用不详；
     */

package org.y1000.item;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

@Slf4j
public final class ItemConfig {

    // "Name,ViewName,Kind,Desc,Grade,QuestNum,NeedItem,NotHaveItem,DelItem,AddItem,boDouble,boColoring,Shape,WearPos,WearShape,ActionImage,HitMotion,HitType,Color,Sex,Weight,NeedGrade,Price,BuyPrice,RepairPrice,ServerId,X,Y,SoundEvent,SoundDrop,boPower,AttackSpeed,Recovery,LongAvoid,Avoid,LongAccuracy,Accuracy,KeepRecovery,DamageBody,DamageHead,DamageArm,DamageLeg,ArmorBody,ArmorHead,ArmorArm,ArmorLeg,RandomCount,NameParam1,NameParam2,Material,JobKind,boUpgrade,MaxUpgrade,boTalentExp,boDurability,Durability,DecDelay,DecSize,Abrasion,ToolConst,SuccessRate,boNotTrade,boNotExchange,boNotDrop,boNotSkill,boNotSSamzie,cLife,Attribute,SpecialKind,RoleType,Script,MaxCount,BoNotSave,ExtJobExp"
    /*
    Name,                名称
ViewName,        显示名称
Kind,                类型

Desc,                文字注释

Grade,                品别

QuestNum,        #寻求号码

NeedItem,        需要该物品才能拾取改物品

NotHaveItem,        拥有该物品时将无法拾取的物品

DelItem,        #删除物品

AddItem,        #添加物品

boDouble,        是否重叠；“TRUE”=是，“FALSE”=否，

boColoring,        是否能染色；不知有啥用，默认“FALSE”

Shape,                外形

WearPos,        装备的部位；9=武器，8=帽子，7=头发，6=衣服，4=裤裙，3=鞋子，2=上衣，1=护腕

WearShape,        装备的显示样式；添代码

ActionImage,        使用的样式；动态图片；

HitMotion,        攻击时的动作；拳头=0，刀/剑/投=2，枪/槌/矿=3，弓=4

HitType,        攻击类型，指装备该物品后使用武功；拳法=0，剑法=1，刀法=2，棰法=3，枪法=4，弓术=5，投法=6，采矿=7

Color,                颜色；

Sex,                性别；男=1，女=2

Weight,                重量；武器/装备=1，书籍/药品/其他=不填

NeedGrade,        需要等级；如何设置不清楚，掌和招式=6 灵动=6 风灵=7

Price,                价钱；

BuyPrice,        卖给NPC的价钱；

RepairPrice,        维修需要的价钱

ServerId,        #在这里不知道啥用，默认=空白

X,Y,                #在这里不知道啥用，默认=空白

SoundEvent,        得到的声音

SoundDrop,        掉落的声音

boPower,        是否拥有能力值；“TRUE”=是，空白或“FALSE”=否

AttackSpeed,        攻击速度；

Recovery,        恢复；

LongAvoid,        远距离躲闪；

Avoid,                躲闪；

您已评论，请查看如下隐藏内容哦！

LongAccuracy,        远距离命中；

Accuracy,        命中；

KeepRecovery,        维持；

DamageBody,        身体攻击；

DamageHead,        头部攻击；

DamageArm,        手部攻击；

DamageLeg,        脚部攻击；

ArmorBody,        身体防御；

ArmorHead,        头部防御；

ArmorArm,        手部防御；

ArmorLeg,        脚部防御；

RandomCount,        随机数字；不详

NameParam1,        不详；

NameParam2,        不详；

Material,        所需材料；制造此物品所需要的材料

JobKind,        制造的职业；空=所有职业，99=NPC，1=铸造，2=炼丹，3=裁缝，4=工匠

boUpgrade,        是否能升级；“TRUE”=是，“FALSE”=否

MaxUpgrade,        升级的最高级别；

boTalentExp,        是否需要才能；“TRUE”=是，“FALSE”或空白=否

boDurability,        是否有耐久；“TRUE”=是，“FALSE”或空白=否

Durability,        耐久度；

DecDelay,        磨损的周期；单位：毫秒

DecSize,        每个磨损周期的单位；即每次磨损几点耐久

Abrasion,        维修后的磨损；指每次维修后就会降低物品的最大耐久度

ToolConst,        维修费用；

SuccessRate,        增加成功率；精华素+20，草芥+40，生死+60

boNotTrade,        是否无法买卖；“TRUE”=无法交易，“FALSE”或空白=可以交易

boNotExchange,        是否无法交易；

boNotDrop,        是否不能丢在地上；

boNotSkill,        是否不能放进技能窗；

boNotSSamzie,        是否不能放进福袋；

cLife,                增加活力；1000 = 10活力

Attribute,        特征；1=本馆不掉血，3=沙漠不掉血，4=五色药水，5=英雄装备，6=招式，199=能用太极牌

SpecialKind,        特殊类型；1=灵符，2=镏金，3=卷轴，4=基本武器，5=王陵3任务物品，6=技能药品，7=+活力，9=百炼

RoleType,        任务类型；不详

Script,                脚本；

MaxCount,        最多持有数量；
     */
    private final Map<String, String[]> values;

    private final Map<String, Integer> headerIndex;

    private static final String ITEM_TYPE = "Kind";

    public ItemConfig(String[] header, Map<String, String[]> values) {
        this.values = values;
        headerIndex = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            headerIndex.put(header[i], i);
        }
    }

    public EquipmentType getEquipmentType(String itemName) {
        return getEnum(itemName, "WearPos", EquipmentType::fromValue);
    }
    public boolean contains(String itemName) {
        return values.containsKey(itemName);
    }

    public AttackKungFuType getAttackKungFuType(String item) {
        return getEnum(item, "HitType", AttackKungFuType::fromValue);
    }

    private <E extends Enum<E>> E getEnum(String itemName, String key, Function<Integer, E> creator) {
        if (!contains(itemName)) {
            throw new NoSuchElementException(itemName + " does not exist.");
        }
        String s = get(itemName, key);
        if (s == null) {
            throw new NoSuchElementException(itemName + ":" + key + " does not exist.");
        }
        try {
            return creator.apply(Integer.parseInt(s));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public ItemType getType(String itemName) {
        return getEnum(itemName, ITEM_TYPE, ItemType::fromValue);
    }

    public String get(String itemName, String key) {
        String[] strings = values.get(itemName);
        if (strings == null) {
            return null;
        }
        Integer index = headerIndex.get(key);
        if (index == null) {
            return null;
        }
        return strings[index];
    }


    public static ItemConfig read() {
        var name = "Item.sdb";
        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource("sdb/" + name);
            if (resource == null) {
                throw new NoSuchElementException("cant get db: " + name);
            }
            URI uri = resource.toURI();
            List<String> lines = Files.readAllLines(Paths.get(uri), Charset.forName("GBK"));
            if (lines.isEmpty()) {
                throw new NoSuchElementException("Empty sdb: " + name);
            }
            String[] header = lines.get(0).split(",");
            if (header.length == 0) {
                throw new NoSuchElementException("Empty sdb: " + name);
            }
            Map<String, String[]> result = new HashMap<>();
            for (int i = 1; i < lines.size(); i++) {
                String item = lines.get(i);
                String[] values = item.split(",");
                if (values.length == 0 || StringUtils.isBlank(values[0])) {
                    log.warn("Invalid line {}", item);
                    continue;
                }
                result.put(values[0], values);
            }
            return new ItemConfig(header, result);
        } catch (URISyntaxException | IOException e) {
            log.error("Failed to read sdb {}.", name, e);
            throw new RuntimeException(e);
        }
    }

}

package org.y1000.kungfu;

import org.apache.commons.lang3.StringUtils;
import org.y1000.item.ItemSdbImpl;
import org.y1000.sdb.AbstractSdbReader;

import java.util.Set;

public final class KungFuSdb extends AbstractSdbReader {

    private KungFuSdb() {
        read("Magic.sdb");
    }

    public static final KungFuSdb INSTANCE = new KungFuSdb();


    // 5Energy,5InPower,5OutPower,5Magic,5Life

    public int get5Energy(String name) {
        return getInt(name, "5Energy");
    }

    public int get5InPower(String name) {
        return getInt(name, "5InPower");
    }

    public int get5OutPower(String name) {
        return getInt(name, "5OutPower");
    }

    public int get5Magic(String name) {
        return getInt(name, "5Magic");
    }

    public int get5Life(String name) {
        return getInt(name, "5Life");
    }

    public int getEEnergy(String name) {
        return getIntOrZero(name, "eEnergy");
    }

    public int getEInPower(String name) {
        return getIntOrZero(name, "eInPower");
    }

    public int getEOutPower(String name) {
        return getIntOrZero(name, "eOutPower");
    }

    public int getEMagic(String name) {
        return getIntOrZero(name, "eMagic");
    }

    public int getELife(String name) {
        return getIntOrZero(name, "eLife");
    }

    //kEnergy,kInPower,kOutPower,kMagic
    public int getKEnergy(String name) {
        return getInt(name, "kEnergy");
    }

    public int getKInPower(String name) {
        return getInt(name, "kInPower");
    }

    public int getKOutPower(String name) {
        return getInt(name, "kOutPower");
    }

    public int getKLife(String name) {
        return getInt(name, "kLife");
    }

    public int getKMagic(String name) {
        return getInt(name, "kMagic");
    }

    public int getAttackSpeed(String name) {
        return getInt(name, "AttackSpeed");
    }

    public int getRecovery(String name) {
        return getInt(name, "Recovery");
    }

    public int getAvoidance(String name) {
        return getInt(name, "Avoid");
    }

    public int getAccuracy(String name) {
        return getInt(name, "accuracy");
    }

    public int getDamageBody(String name) {
        return getInt(name, "DamageBody");
    }

    public int getDamageHead(String name) {
        return getInt(name, "DamageHead");
    }

    public int getDamageArm(String name) {
        return getInt(name, "DamageArm");
    }

    public int getDamageLeg(String name) {
        return getInt(name, "DamageLeg");
    }


    public int getArmorBody(String name) {
        return getInt(name, "ArmorBody");
    }

    public int getArmorHead(String name) {
        return getInt(name, "ArmorHead");
    }

    public int getArmorArm(String name) {
        return getInt(name, "ArmorArm");
    }

    public int getArmorLeg(String name) {
        return getInt(name, "ArmorLeg");
    }

    public String getSoundSwing(String name) {
        return get(name, "SoundSwing");
    }

    public String getSoundStrike(String name) {
        return get(name, "SoundStrike");
    }

    public String getSoundStart(String name) {
        return get(name, "SoundStart");
    }

    public String getSoundEnd(String name) {
        return get(name, "SoundEnd");
    }

    public String getSoundEvent(String name) {
        return get(name, "SoundEvent");
    }

    public String getBowImage(String name) {
        return get(name, "BowImage");
    }

    public KungFuType getMagicType(String name) {
        return getEnum(name, "MagicType", KungFuType::fromValue);
    }

    public int effectColor(String name) {
        return getInt(name, "EffectColor");
    }

    public static void main(String[] args) {
//        System.out.println(INSTANCE.get("无名剑法", "SoundSwing"));
//        System.out.println(INSTANCE.get("无名剑法", "SoundStrike"));

        KungFuSdb kungFuSdb = KungFuSdb.INSTANCE;
//        Set<String> names = kungFuSdb.names();
        Set<String> names = kungFuSdb.columnNames();
        Set<String> items = kungFuSdb.names();
        for (String i : items) {
            if (!i.contains("壁射剑法"))
                continue;

            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(kungFuSdb.get(i, name)))
                    System.out.println(name + ": " + kungFuSdb.get(i, name));
            }
            //String v = kungFuSdb.get("生药", name);
        }
    }

}

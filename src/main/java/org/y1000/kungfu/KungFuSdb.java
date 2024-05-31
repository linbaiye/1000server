package org.y1000.kungfu;

import org.y1000.sdb.AbstractSdbReader;

public final class KungFuSdb extends AbstractSdbReader {

    private KungFuSdb() {}

    public static final KungFuSdb INSTANCE = Load();

    private static KungFuSdb Load() {
        KungFuSdb sdb = new KungFuSdb();
        sdb.read("Magic.sdb");
        return sdb;
    }

    public int getAttackSpeed(String name) {
        return getInt(name, "AttackSpeed");
    }
    /*
    AttackSpeed,Recovery,KeepRecovery,Avoid,accuracy,DamageBody,DamageHead,DamageArm,DamageLeg,DamageEnergy,ArmorBody,ArmorHead,ArmorArm,ArmorLeg
     */

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

    public KungFuType getMagicType(String name) {
        return getEnum(name, "MagicType", KungFuType::fromValue);
    }
}

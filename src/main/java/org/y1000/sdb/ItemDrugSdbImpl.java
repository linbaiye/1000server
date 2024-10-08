package org.y1000.sdb;

public final class ItemDrugSdbImpl extends AbstractCSVSdbReader implements ItemDrugSdb {

    public static final ItemDrugSdb INSTANCE = new ItemDrugSdbImpl();

    private ItemDrugSdbImpl() {
        read("ItemDrug.sdb", "utf8");
    }

    /*
    Name,Type,UseInterval,UseCount,StillInterval,eEnergy,eInPower,eOutPower,eMagic,eLife,eHeadLife,eArmLife,eLegLife,DamageBody,DamageHead,DamageArm,DamageLeg,ArmorBody,ArmorHead,ArmorArm,ArmorLeg,AttackSpeed,Avoid,Recovery,Accuracy,KeepRecovery,LightDark,
     */

    @Override
    public int getUseInterval(String itemName) {
        return getInt(itemName, "UseInterval");
    }

    @Override
    public int getUseCount(String itemName) {
        return getInt(itemName, "UseCount");
    }

    @Override
    public int getStillInterval(String itemName) {
        return getInt(itemName, "StillInterval");
    }

    @Override
    public int getEEnergy(String name) {
        return getIntOrZero(name, "eEnergy");
    }

    @Override
    public int getEInPower(String name) {
        return getIntOrZero(name, "eInPower");
    }

    @Override
    public int getEOutPower(String name) {
        return getIntOrZero(name, "eOutPower");
    }

    @Override
    public int getEMagic(String name) {
        return getIntOrZero(name, "eMagic");
    }

    @Override
    public int getELife(String name) {
        return getIntOrZero(name, "eLife");
    }

    @Override
    public int getEHeadLife(String name) {
        return getIntOrZero(name, "eHeadLife");
    }

    @Override
    public int getEArmLife(String name) {
        return getIntOrZero(name, "eArmLife");
    }

    @Override
    public int getELegLife(String name) {
        return getIntOrZero(name, "eLegLife");
    }

    @Override
    public int getDamageBody(String name) {
        return getIntOrZero(name, "DamageBody");
    }
}

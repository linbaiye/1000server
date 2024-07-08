package org.y1000.item;

import org.y1000.sdb.ItemDrugSdb;

public final class PillAttributeProviderImpl implements PillAttributeProvider {
    private final String name;
    private final ItemSdb itemSdb;

    private final ItemDrugSdb itemDrugSdb;

    public PillAttributeProviderImpl(String name,
                                     ItemSdb itemSdb,
                                     ItemDrugSdb itemDrugSdb) {
        this.name = name;
        this.itemSdb = itemSdb;
        this.itemDrugSdb = itemDrugSdb;
    }

    @Override
    public int useInterval() {
        return itemDrugSdb.getUseInterval(name);
    }

    @Override
    public int useCount() {
        return itemDrugSdb.getUseCount(name);
    }

    @Override
    public int power() {
        return itemDrugSdb.getEMagic(name);
    }

    @Override
    public int innerPower() {
        return itemDrugSdb.getEInPower(name);
    }

    @Override
    public int outerPower() {
        return itemDrugSdb.getEOutPower(name);
    }

    @Override
    public int life() {
        return itemDrugSdb.getELife(name);
    }

    @Override
    public int headLife() {
        return itemDrugSdb.getEHeadLife(name);
    }

    @Override
    public int armLife() {
        return itemDrugSdb.getEArmLife(name);
    }

    @Override
    public int legLife() {
        return itemDrugSdb.getELegLife(name);
    }

    @Override
    public String dropSound() {
        return itemSdb.getSoundDrop(name);
    }

    @Override
    public String eventSound() {
        return itemSdb.getSoundEvent(name);
    }
}

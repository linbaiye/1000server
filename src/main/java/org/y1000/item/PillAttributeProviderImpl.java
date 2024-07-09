package org.y1000.item;

import org.y1000.sdb.ItemDrugSdb;

public final class PillAttributeProviderImpl implements PillAttributeProvider {
    private final String name;
    private final ItemSdb itemSdb;

    private final ItemDrugSdb itemDrugSdb;

    private static final int ITEMDRUG_DIV_VALUE            = 10;

    private static final int ITEMDRUG_MUL_EVENTENERGY      = 10;
    private static final int ITEMDRUG_MUL_EVENTINPOWER     = 10;
    private static final int ITEMDRUG_MUL_EVENTOUTPOWER    = 10;
    private static final int ITEMDRUG_MUL_EVENTMAGIC       = 10;
    private static final int ITEMDRUG_MUL_EVENTLIFE        = 15;
    private static final int ITEMDRUG_MUL_EVENTHEADLIFE    = 15;
    private static final int ITEMDRUG_MUL_EVENTARMLIFE     = 15;
    private static final int ITEMDRUG_MUL_EVENTLEGLIFE     = 15;

    private final int life;
    private final int power;
    private final int innerPower;
    private final int outerPower;
    private final int armLife;
    private final int headLife;
    private final int legLife;
    private final int interval;

    public PillAttributeProviderImpl(String name,
                                     ItemSdb itemSdb,
                                     ItemDrugSdb itemDrugSdb) {
        this.name = name;
        this.itemSdb = itemSdb;
        this.itemDrugSdb = itemDrugSdb;
        this.life = itemDrugSdb.getELife(name) * ITEMDRUG_MUL_EVENTLIFE / ITEMDRUG_DIV_VALUE;
        this.power = itemDrugSdb.getEMagic(name) * ITEMDRUG_MUL_EVENTMAGIC / ITEMDRUG_DIV_VALUE;
        this.innerPower = itemDrugSdb.getEInPower(name) * ITEMDRUG_MUL_EVENTINPOWER / ITEMDRUG_DIV_VALUE;
        this.outerPower = itemDrugSdb.getEOutPower(name) * ITEMDRUG_MUL_EVENTOUTPOWER / ITEMDRUG_DIV_VALUE;
        this.armLife = itemDrugSdb.getEArmLife(name) * ITEMDRUG_MUL_EVENTARMLIFE / ITEMDRUG_DIV_VALUE;
        this.headLife = itemDrugSdb.getEHeadLife(name) * ITEMDRUG_MUL_EVENTHEADLIFE / ITEMDRUG_DIV_VALUE;
        this.legLife = itemDrugSdb.getELegLife(name) * ITEMDRUG_MUL_EVENTLEGLIFE / ITEMDRUG_DIV_VALUE;
        this.interval = itemDrugSdb.getUseInterval(name) * 10;
    }

    @Override
    public int useInterval() {
        return interval;
    }

    @Override
    public int useCount() {
        return itemDrugSdb.getUseCount(name);
    }

    @Override
    public int power() {
        return power;
    }

    @Override
    public int innerPower() {
        return innerPower;
    }

    @Override
    public int outerPower() {
        return outerPower;
    }

    @Override
    public int life() {
        return life;
    }

    @Override
    public int headLife() {
        return headLife;
    }

    @Override
    public int armLife() {
        return armLife;
    }

    @Override
    public int legLife() {
        return legLife;
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

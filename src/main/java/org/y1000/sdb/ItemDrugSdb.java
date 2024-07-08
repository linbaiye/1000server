package org.y1000.sdb;

public interface ItemDrugSdb {
    int getUseInterval(String itemName);

    int getUseCount(String itemName);

    int getStillInterval(String itemName);

    int getEEnergy(String name);

    int getEInPower(String name);

    int getEOutPower(String name);

    int getEMagic(String name);

    int getELife(String name);

    int getEHeadLife(String name);

    int getEArmLife(String name);

    int getELegLife(String name);
}

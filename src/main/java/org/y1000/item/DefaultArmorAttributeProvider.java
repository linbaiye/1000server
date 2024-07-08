package org.y1000.item;

public final class DefaultArmorAttributeProvider implements ArmorItemAttributeProvider {
    private final String itemName;

    private final ItemSdb itemSdb;

    public DefaultArmorAttributeProvider(String itemName,
                                         ItemSdb itemSdb) {
        this.itemName = itemName;
        this.itemSdb = itemSdb;
    }

    @Override
    public String dropSound() {
        return itemSdb.getSoundDrop(itemName);
    }

    @Override
    public String eventSound() {
        return itemSdb.getSoundEvent(itemName);
    }

    @Override
    public int avoidance() {
        return itemSdb.getAvoid(itemName);
    }

    @Override
    public int headArmor() {
        return itemSdb.getArmorHead(itemName);
    }

    @Override
    public int armor() {
        return itemSdb.getArmorBody(itemName);
    }

    @Override
    public int armArmor() {
        return itemSdb.getArmorArm(itemName);
    }

    @Override
    public int legArmor() {
        return itemSdb.getArmorLeg(itemName);
    }

    @Override
    public int recovery() {
        return itemSdb.getRecovery(itemName);
    }

    @Override
    public boolean isMale() {
        return itemSdb.isMale(itemName);
    }
}

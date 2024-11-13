package org.y1000.item;

import org.y1000.entities.players.Armor;

import java.util.Set;

public final class ArmorImpl extends AbstractSexualEquipment implements ArmorEquipment {

    private final ItemSdb itemSdb;

    private final Armor originArmor;

    public ArmorImpl(String name,
                     ItemSdb itemSdb,
                     Set<Object> abilities) {
        super(name, itemSdb, abilities);
        this.itemSdb = itemSdb;
        this.originArmor =  new Armor(itemSdb.getArmorBody(name), itemSdb.getArmorHead(name), itemSdb.getArmorArm(name), itemSdb.getArmorLeg(name));
    }

    @Override
    public Armor armor() {
        return findAbility(Upgradable.class)
                .map(upgradable -> originArmor.add(originArmor.multiply(upgradable.percentage())))
                .orElse(originArmor);
    }

    private int getOriginAvoid() {
        return itemSdb.getAvoid(name());
    }

    @Override
    public int avoidance() {
        return findAbility(Upgradable.class)
                .map(upgradable -> getOriginAvoid() + (int)(getOriginAvoid() * upgradable.percentage()))
                .orElseGet(this::getOriginAvoid);
    }

    @Override
    public int recovery() {
        return itemSdb.getRecovery(name());
    }

    @Override
    public EquipmentType equipmentType() {
        var type = itemSdb.getEquipmentType(name());
        return type != EquipmentType.WRIST_CHESTED ? type : EquipmentType.WRIST;
    }
}

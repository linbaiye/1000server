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
    @Override
    public String description() {
        StringBuilder descriptionBuilder = getDescriptionBuilder();
        descriptionBuilder.append("恢复: ").append(recovery()).append("\t")
                .append("闪躲: ").append(avoidance()).append("\n");
        Armor armor = armor();
        descriptionBuilder.append(String.format("防御力: %d / %d / %d / %d", armor.body(), armor.head(), armor.arm(), armor.leg()));
        return descriptionBuilder.toString();
    }
}

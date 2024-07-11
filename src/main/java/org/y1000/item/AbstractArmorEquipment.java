package org.y1000.item;


import org.y1000.entities.players.Armor;

public abstract class AbstractArmorEquipment extends AbstractSexualEquipment {
    private final ArmorItemAttributeProvider attributeProvider;

    public AbstractArmorEquipment(String name,
                                  ArmorItemAttributeProvider attributeProvider) {
        super(name, attributeProvider.dropSound(),
                attributeProvider.eventSound(), attributeProvider.description(), attributeProvider.isMale());
        this.attributeProvider = attributeProvider;
    }

    public int avoidance() {
        return attributeProvider.avoidance();
    }

    public Armor armor() {
        return new Armor(attributeProvider.armor(), attributeProvider.headArmor(), attributeProvider.armArmor(), attributeProvider.legArmor());
    }

    public int recovery() {
        return attributeProvider.recovery();
    }
}

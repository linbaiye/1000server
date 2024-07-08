package org.y1000.item;


public abstract class AbstractArmorEquipment extends AbstractEquipment {

    private final ArmorItemAttributeProvider attributeProvider;

    public AbstractArmorEquipment(String name,
                                  ArmorItemAttributeProvider attributeProvider) {
        super(name, attributeProvider.dropSound(),
                attributeProvider.eventSound());
        this.attributeProvider = attributeProvider;
    }

    public boolean isMale() {
        return attributeProvider.isMale();
    }

    public int avoidance() {
        return attributeProvider.avoidance();
    }

    public int headArmor() {
        return attributeProvider.headArmor();
    }


    public int armor() {
        return attributeProvider.armor();
    }

    public int armArmor() {
        return attributeProvider.armArmor();
    }

    public int legArmor() {
        return attributeProvider.legArmor();
    }

    public int recovery() {
        return attributeProvider.recovery();
    }
}

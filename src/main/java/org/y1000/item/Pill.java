package org.y1000.item;


public final class Pill extends AbstractStackItem {
    private final PillAttributeProvider attributeProvider;

    public Pill(String name,
                long number, PillAttributeProvider attributeProvider) {
        super(name, number, ItemType.PILL, attributeProvider.dropSound(),
                attributeProvider.eventSound(), attributeProvider.eventSound());
        this.attributeProvider = attributeProvider;
    }

    public int useInterval() {
        return attributeProvider.useInterval();
    }

    public int useCount() {
        return attributeProvider.useCount();
    }

    public int power() {
        return attributeProvider.power();
    }

    public int innerPower() {
        return attributeProvider.innerPower();
    }

    public int outerPower() {
        return attributeProvider.outerPower();
    }

    public int life() {
        return attributeProvider.life();
    }

    public int headLife() {
        return attributeProvider.headLife();
    }

    public int armLife() {
        return attributeProvider.armLife();
    }

    public int legLife() {
        return attributeProvider.legLife();
    }

    @Override
    public String description() {
        return attributeProvider.description();
    }
}

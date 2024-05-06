package org.y1000.entities.attribute;

public record ArmorAttribute(int bodyArmor, int headArmor, int armArmor, int legArmor) {
    public static final ArmorAttribute DEFAULT = new ArmorAttribute(0, 0, 0, 0);
}

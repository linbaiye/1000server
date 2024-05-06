package org.y1000.entities.attribute;

public record DamageAttribute(int bodyDamage, int headDamage, int armDamage, int legDamage) {

    public static final DamageAttribute DEFAULT = new DamageAttribute(41,41,41,41);

}

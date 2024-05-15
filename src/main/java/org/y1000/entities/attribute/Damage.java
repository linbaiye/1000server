package org.y1000.entities.attribute;

public record Damage(int bodyDamage, int headDamage, int armDamage, int legDamage) {

    public static final Damage DEFAULT = new Damage(41,41,41,41);

}

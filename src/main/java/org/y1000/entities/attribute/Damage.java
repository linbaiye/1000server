package org.y1000.entities.attribute;

import org.apache.commons.lang3.Validate;


public record Damage(int bodyDamage, int headDamage, int armDamage, int legDamage) {

    public Damage {
        Validate.isTrue(bodyDamage >= 0);
        Validate.isTrue(headDamage >= 0);
        Validate.isTrue(armDamage >= 0);
        Validate.isTrue(legDamage >= 0);
    }

    public static final Damage DEFAULT = new Damage(41,41,41,41);

    public Damage add(Damage another) {
        return new Damage(bodyDamage + another.bodyDamage,
                headDamage + another.headDamage,
                armDamage + another.armDamage,
                legDamage + another.legDamage);
    }

}

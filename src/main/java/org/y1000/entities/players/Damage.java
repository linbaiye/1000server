package org.y1000.entities.players;

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

    public Damage multiply(float m) {
        return new Damage((int)(bodyDamage * m), (int)(headDamage * m), (int)(armDamage * m), (int)(legDamage * m));
    }

    public static final Damage ZERO = new Damage(0, 0, 0, 0);

    public boolean equalTo(Damage damage) {
        return damage != null &&
                damage.legDamage == legDamage &&
                damage.bodyDamage == bodyDamage &&
                damage.headDamage == headDamage &&
                damage.armDamage == armDamage;
    }

}

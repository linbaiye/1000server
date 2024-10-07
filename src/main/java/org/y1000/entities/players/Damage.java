package org.y1000.entities.players;



public record Damage(int bodyDamage, int headDamage, int armDamage, int legDamage) {

    public static final Damage DEFAULT = new Damage(41,41,41,41);

    public Damage add(Damage another) {
        return new Damage(bodyDamage + another.bodyDamage,
                headDamage + another.headDamage,
                armDamage + another.armDamage,
                legDamage + another.legDamage);
    }


    public Damage addNoNegative(Damage another) {
        if (another == null)
            return this;
        Damage added = add(another);
        return new Damage(Math.max(added.bodyDamage, 0), Math.max(added.headDamage, 0),
                Math.max(added.armDamage, 0), Math.max(added.legDamage, 0));
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

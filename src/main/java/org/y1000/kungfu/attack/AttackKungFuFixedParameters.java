package org.y1000.kungfu.attack;

/**
 * Parameters that will never change and can be shared by all attack KungFu instances.
 */
public interface AttackKungFuFixedParameters {
    int powerToSwing();

    int innerPowerToSwing();

    int recovery();

    int outerPowerToSwing();

    int lifeToSwing();

    default int bodyDamage() {
        return 1;
    }

    default int attackSpeed() {
        return 100;
    }

    default int avoidance() {
        return 0;
    }

    default int headDamage() {
        return 1;
    }

    default int armDamage() {
        return 1;
    }

    default int legDamage() {
        return 1;
    }

    default int strikeSound() {
        return 0;
    }

    default int swingSound() {
        return 0;
    }

    default int bodyArmor() {
        return 0;
    }

    default int headArmor() {
        return 0;
    }

    default int armArmor() {
        return 0;
    }

    default int legArmor() {
        return 0;
    }

}

package org.y1000.entities.players;

public interface PlayerInnateAttributesProvider {
    default int avoidance() {
        return 25;
    }

    default int attackSpeed() {
        return 70;
    }

    default int life() {
        return 2000;
    }

    default int power() {
        return 500;
    }

    default int innerPower() {
        return 1000;
    }

    default int outerPower() {
        return 1000;
    }

    default int recovery() {
        return 50;
    }

    default Damage damage() {
        return Damage.DEFAULT;
    }

    default int hit() {
        return 75;
    }

}

package org.y1000.entities.players;

import org.y1000.entities.PlayerAttribute;
import org.y1000.exp.Experience;

public final class PlayerExperiencedAttribute implements PlayerAttribute {

    private final int innateValue;

    private Experience experience;

    private int currentValue;

    private int maxValue;

    private int age;

    private boolean gainExpIfFillUp;

    public PlayerExperiencedAttribute(int innateValue, int age) {
        this(innateValue, 0, age);
    }

    public PlayerExperiencedAttribute(int innateValue, int exp, int age) {
        this.innateValue = innateValue;
        experience = new Experience(exp);
        this.age = age;
        computeMaxValue();
        currentValue = maxValue;
        gainExpIfFillUp = false;
    }

    public int currentValue() {
        return currentValue;
    }

    public int maxValue() {
        return maxValue ;
    }

    public void consume(int value) {
        if (value <= 0) {
            return;
        }
        this.currentValue = Math.max(currentValue - value, 0);
        if (currentValue <= maxValue() * 0.1) {
            gainExpIfFillUp = true;
        }
    }

    public boolean hasEnough(int v) {
        return currentValue >= v;
    }

    private void computeMaxValue() {
        maxValue = experience.level() + innateValue + age / 2;
    }

    public void gain(int value) {
        if (value <= 0) {
            return;
        }
        this.currentValue = Math.min(maxValue, currentValue + value);
        if (!gainExpIfFillUp || currentValue < maxValue() * 0.9) {
            return;
        }
        experience = experience.gainDefaultExp();
        gainExpIfFillUp = false;
        computeMaxValue();
    }

    public boolean onAgeIncreased(int newAge) {
        if (this.age >= newAge) {
            return false;
        }
        this.age = newAge;
        int old = maxValue();
        computeMaxValue();
        return old != maxValue();
    }
}

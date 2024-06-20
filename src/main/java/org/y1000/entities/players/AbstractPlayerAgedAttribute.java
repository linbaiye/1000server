package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;

public abstract class AbstractPlayerAgedAttribute implements PlayerAgedAttribute {
    private final int innateValue;

    private int currentValue;

    private int maxValue;

    private int age;

    public AbstractPlayerAgedAttribute(int innateValue,
                                       int maxValue,
                                       Integer currentValue,
                                       int age) {
        Validate.isTrue(maxValue > 0);
        Validate.isTrue(innateValue >= 0);
        Validate.isTrue(age >= 0);
        this.innateValue = innateValue;
        this.age = age;
        this.maxValue = maxValue;
        this.currentValue = currentValue != null ? currentValue : maxValue;
    }

    protected abstract int computeMaxValue();

    protected int innateValue() {
        return innateValue;
    }

    public int currentValue() {
        return currentValue;
    }

    public int maxValue() {
        return maxValue ;
    }

    protected boolean doConsume(int value) {
        if (value <= 0) {
            return false;
        }
        this.currentValue = Math.max(currentValue - value, 0);
        return true;
    }

    public boolean hasEnough(int v) {
        return currentValue >= v;
    }

    protected boolean doGain(int value) {
        if (value <= 0) {
            return false;
        }
        this.currentValue = Math.min(maxValue, currentValue + value);
        return true;
    }

    protected int age() {
        return age;
    }

    protected void updateMaxValue() {
        maxValue = computeMaxValue();
    }

    public boolean onAgeIncreased(int newAge) {
        if (this.age >= newAge) {
            return false;
        }
        this.age = newAge;
        int old = maxValue();
        updateMaxValue();
        return old != maxValue();
    }

}

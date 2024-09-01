package org.y1000.entities.players;

import org.y1000.exp.Experience;

public final class PlayerExperiencedAgedAttribute extends AbstractPlayerAgedAttribute {

    private Experience experience;

    private boolean gainExpIfFillUp;


    public PlayerExperiencedAgedAttribute(int innateValue, int age) {
        this(innateValue, new Experience(0), null, age);
    }

    public PlayerExperiencedAgedAttribute(int innateValue, int exp, Integer current, int age) {
        this(innateValue, new Experience(exp), current, age);
    }

    public PlayerExperiencedAgedAttribute(int innateValue, Experience experience, Integer current, int age) {
        super(innateValue, experience.level()  + innateValue + age / 2, current, age);
        this.experience = experience;
        gainExpIfFillUp = false;
    }

    public int exp() {
        return experience.value();
    }


    public void consume(int value) {
        if (!doConsume(value)) {
            return;
        }
        if (currentValue() <= maxValue() * 0.1) {
            gainExpIfFillUp = true;
        }
    }

    protected int computeMaxValue() {
        return experience.level() + innateValue() + age() / 2;
    }

    public void gain(int value) {
        if (!doGain(value)) {
            return;
        }
        if (!gainExpIfFillUp || currentValue() < maxValue() * 0.9) {
            return;
        }
        experience = experience.gainDefaultExp();
        gainExpIfFillUp = false;
        updateMaxValue();
    }


    public static PlayerExperiencedAgedAttribute createInnerPower() {
        return new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.innerPower(), 100);
    }

    public static PlayerExperiencedAgedAttribute createOuterPower() {
        return new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.outerPower(), 100);
    }

    public static PlayerExperiencedAgedAttribute createPower() {
        return new PlayerExperiencedAgedAttribute(PlayerDefaultAttributes.INSTANCE.power(), 100);
    }

}

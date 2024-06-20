package org.y1000.entities.players;

import org.y1000.exp.Experience;

public final class PlayerExperiencedAgedAttribute extends AbstractPlayerAgedAttribute {

    private Experience experience;

    private boolean gainExpIfFillUp;

    private final String name;

    public PlayerExperiencedAgedAttribute(String name, int innateValue, int age) {
        this(name, innateValue, new Experience(0), null, age);
    }

    public PlayerExperiencedAgedAttribute(String name, int innateValue, Experience experience, Integer current, int age) {
        super(innateValue, experience.level()  + innateValue + age / 2, current, age);
        this.name = name;
        this.experience = experience;
        gainExpIfFillUp = false;
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
        return new PlayerExperiencedAgedAttribute("内功", PlayerDefaultAttributes.INSTANCE.innerPower(), 100);
    }

    public static PlayerExperiencedAgedAttribute createOuterPower() {
        return new PlayerExperiencedAgedAttribute("外功", PlayerDefaultAttributes.INSTANCE.outerPower(), 100);
    }

    public static PlayerExperiencedAgedAttribute createPower() {
        return new PlayerExperiencedAgedAttribute("武功", PlayerDefaultAttributes.INSTANCE.power(), 100);
    }

}

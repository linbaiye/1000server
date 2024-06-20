package org.y1000.entities.players;


import org.y1000.exp.ExperienceUtil;

public final class PlayerLife extends AbstractPlayerAgedAttribute {
    private static final int LEVEL = ExperienceUtil.computeLevel(0);

    public PlayerLife(int innateValue, int age) {
        this(innateValue, age, null);
    }

    public PlayerLife(int innateValue, int age, Integer current) {
        super(innateValue, innateValue + age + LEVEL, current, age);
    }

    @Override
    public void consume(int v) {
        doConsume(v);
    }

    @Override
    public void gain(int value) {
        doGain(value);
    }

    @Override
    protected int computeMaxValue() {
        return innateValue() + age() + LEVEL;
    }

    public static PlayerLife create() {
        return new PlayerLife(PlayerDefaultAttributes.INSTANCE.life(), 100, null);
    }
}

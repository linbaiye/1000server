package org.y1000.entities.players;

public class PlayerTestingAttribute extends AbstractPlayerAgedAttribute {

    private int maxValue;

    public PlayerTestingAttribute(int max) {
        super(0, max, null, 0);
    }

    public static PlayerExperiencedAgedAttribute of(int max) {
        int age = 0;
        PlayerExperiencedAgedAttribute attribute = new PlayerExperiencedAgedAttribute(0, age);
        while(attribute.maxValue() < max) {
            attribute.onAgeIncreased(age++);
        }
        return new PlayerExperiencedAgedAttribute(0, attribute.exp(), max, age);
    }

    @Override
    protected int computeMaxValue() {
        return maxValue;
    }

    @Override
    public void consume(int v) {
        doConsume(v);
    }

    @Override
    public void gain(int value) {
        doGain(value);
    }
}

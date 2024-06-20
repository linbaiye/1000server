package org.y1000.entities.players;

public class PlayerTestingAttribute extends AbstractPlayerAgedAttribute {

    private int maxValue;

    public PlayerTestingAttribute(int max) {
        super(0, max, null, 0);
    }

    public static PlayerTestingAttribute of(int max) {
        return new PlayerTestingAttribute(max);
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

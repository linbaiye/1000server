package org.y1000.entities.players;

public interface PlayerAgedAttribute {
    int currentValue();

    int maxValue();

    void consume(int v);

    default int percent() {
        return maxValue() > 0 ? currentValue() * 100 / maxValue() : 0;
    }

    void gain(int value);

    boolean hasEnough(int v);

    boolean onAgeIncreased(int newAge);
}

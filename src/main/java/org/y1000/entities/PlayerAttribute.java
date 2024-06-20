package org.y1000.entities;

public interface PlayerAttribute {
    int currentValue();

    int maxValue();

    void consume(int v);

    void gain(int value);

    boolean hasEnough(int v);

}

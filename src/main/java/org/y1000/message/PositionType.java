package org.y1000.message;

public enum PositionType implements ValueEnum{
    MOVE(1),
    TURN(2),
    SET(2),
    ;
    private final int v;
    PositionType(int v) {
        this.v = v;
    }
    @Override
    public int value() {
        return v;
    }



}

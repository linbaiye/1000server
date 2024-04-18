package org.y1000.message;

public enum MovementType implements ValueEnum{
    MOVE(1),
    SET(3),
    RUN(4),
    FLY(5),
    ;
    private final int v;
    MovementType(int v) {
        this.v = v;
    }
    @Override
    public int value() {
        return v;
    }
}

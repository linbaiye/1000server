package org.y1000.message;

public enum PositionType implements ValueEnum {
    MOVE(1),
    SET(3),
    DRAGGED(4),
    REWIND(6),
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

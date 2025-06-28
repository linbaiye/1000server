package org.y1000.entities.creatures.monster;

public enum NpcStateEnum {

    Move(1),
    Idle(2),
    Hurt(3),
    Attack(4),
    Die(5),

    Turn(6),
    ;

    private final int v;

    NpcStateEnum(int v) {
        this.v = v;
    }

    public int value( ) {
        return v;
    }
}

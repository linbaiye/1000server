package org.y1000.entities.creatures.npc;

public enum NpcAction {

    Move(1),
    Idle(2),
    Hurt(3),
    Attack(4),
    Die(5),

    Turn(6),
    ;

    private final int v;

    NpcAction(int v) {
        this.v = v;
    }

    public int value( ) {
        return v;
    }
}

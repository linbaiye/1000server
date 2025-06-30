package org.y1000.entities.players;

import org.y1000.message.ValueEnum;

public enum PlayerStateEnum implements ValueEnum {
    Idle(1),
    Move(2),
    Attack(3),
    FightStand(8),
    Sit(10),
    StandUp(4),
    Hurt(6),
    Die(7),
    Hello(14),
    Turn(24),
    ;
    private final int v;

    PlayerStateEnum(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }
}

package org.y1000.entities.players;

import lombok.Getter;
import org.y1000.util.ValueEnum;

public enum AttackAction implements ValueEnum  {
    Punch(1, 400),
    Kick(2, 560),
    Sword1H(3, 720),
    Sword2H(4, 800),
    Blade1H(5, 720),
    Blade2H(6, 630),
    Axe(7, 800),
    Spear(8, 800),
    Bow(9, 600),
    Throw(10, 900),
    ;

    private final int v;

    // How long this action takes.
    @Getter
    private final int millis;

    AttackAction(int v, int millis) {
        this.v = v;
        this.millis = millis;
    }

    @Override
    public int value() {
        return v;
    }
}

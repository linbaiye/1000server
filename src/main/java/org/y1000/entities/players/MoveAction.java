package org.y1000.entities.players;


import lombok.Getter;
import org.y1000.message.ValueEnum;

public enum MoveAction implements ValueEnum  {
    Walk(0, 840),
    FightWalk(1, 840),
    Run(2, 420),
    Fly(3, 360),
    ;
    private final int v;
    @Getter
    private final int millis;

    MoveAction(int v, int millis) {
        this.v = v;
        this.millis = millis;
    }

    @Override
    public int value() {
        return v;
    }



}

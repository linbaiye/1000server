package org.y1000.entities.players;


import org.y1000.message.ValueEnum;

public enum MoveAction implements ValueEnum  {
    Walk(0),
    FightWalk(1),
    Run(2),
    Fly(3),
    ;
    private final int v;

    MoveAction(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }
}

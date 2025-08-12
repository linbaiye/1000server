package org.y1000.entities.npc;

import lombok.Getter;

public class NpcAnimation {

    private int actionMillis;

    private int elapsedMillis;

    @Getter
    private final int actualMillis;

    private final NpcAction action;

    public NpcAnimation(int length,
                        NpcAction animationEnum) {
        this.action = animationEnum;
        this.actualMillis = length;
    }

    public boolean update(int delta) {
        if (elapsedMillis >= actionMillis)
            return true;
        elapsedMillis += delta;
        return elapsedMillis >= actionMillis;
    }

    public int elapsedMillis() {
        return elapsedMillis;
    }

    public NpcAction type() {
        return action;
    }

    public void start(int actionMillis) {
        this.actionMillis = actionMillis;
        elapsedMillis = 0;
    }

    public void start() {
        start(actualMillis);
    }
}

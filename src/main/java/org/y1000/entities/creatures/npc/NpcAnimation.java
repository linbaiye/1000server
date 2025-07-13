package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;

public class NpcAnimation {

    private int actionMillis;

    private int elapsedMillis;

    @Getter
    private final int actualMillis;

    private final NpcAnimationEnum animationEnum;

    public NpcAnimation(int length,
                        NpcAnimationEnum animationEnum) {
        this.animationEnum = animationEnum;
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

    public NpcAnimationEnum type() {
        return animationEnum;
    }

    public void start(int actionMillis) {
        this.actionMillis = actionMillis;
        elapsedMillis = 0;
    }

    public void start() {
        start(actualMillis);
    }
}

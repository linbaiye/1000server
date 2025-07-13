package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.message.NpcSnapshot;

public abstract class AbstractNpcAbility implements NpcAbility {
    private final NpcAnimation animation;

    public AbstractNpcAbility(NpcAnimation animation) {
        this.animation = animation;
    }

    void startAnimation() {
        animation.start();
    }

    void startAnimation(int millis) {
        animation.start(millis);
    }

    NpcAnimationEnum type() {
        return animation.type();
    }

    void startShorter(int millis) {
        animation.start(Math.min(millis, animation.getActualMillis()));
    }

    boolean updateAnimation(int delta) {
        return animation.update(delta);
    }

    @Override
    public NpcSnapshot captureSnapshot(Npc npc) {
        return NpcSnapshot.of(npc, animation.elapsedMillis(), animation.type());
    }
}

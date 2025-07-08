package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.message.NpcSnapshot;

public abstract class AbstractNpcAbility implements NpcAbility {
    private final NpcAnimation animationTimer;

    public AbstractNpcAbility(NpcAnimation animation) {
        this.animationTimer = animation;
    }

    void startAnimation() {
        animationTimer.start();
    }

    void startAnimation(int millis) {
        animationTimer.start(millis);
    }

    NpcAnimationEnum type() {
        return animationTimer.type();
    }

    boolean updateAnimation(int delta) {
        return animationTimer.update(delta);
    }

    @Override
    public NpcSnapshot captureSnapshot(Npc npc) {
        return NpcSnapshot.of(npc, animationTimer.elapsedMillis(), animationTimer.type());
    }
}

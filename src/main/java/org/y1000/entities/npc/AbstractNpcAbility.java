package org.y1000.entities.npc;


public abstract class AbstractNpcAbility implements NpcAnimatedAbility {
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

    NpcAnimation getAnimation() {
        return animation;
    }

    NpcAction type() {
        return animation.type();
    }

    void startShorter(int millis) {
        animation.start(Math.min(millis, animation.getActualMillis()));
    }

    boolean updateAnimation(int delta) {
        return animation.update(delta);
    }

}

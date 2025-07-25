package org.y1000.entities.objects;

import java.util.List;

public class OpenAbility implements DynamicAbility {

    private int openMillis;

    private final Animation[] animations;

    private int current;


    public OpenAbility(int openMillis,
                       List<Animation> animationList) {
        this.openMillis = openMillis;
        this.animations = animationList.toArray(new Animation[0]);
        current = 0;
    }

    public void update(DynamicObject dynamicObject, int delta) {
        if (!animations[current].elapse(delta)) {
            return;
        }
        if (animations.length > current + 1) {
            current++;
            // another animation.
        }
        openMillis--;
        if (openMillis <= 0)
            //remove;
    }

    @Override
    public Animation currentAnimation() {
        return animations[current];
    }

    @Override
    public void collectAnimations(List<Animation> collector) {
        collector.addAll(List.of(animations));
    }
}

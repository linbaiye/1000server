package org.y1000.entities.objects;

import java.util.List;

public class StaticAbility implements DynamicAbility {
    private final Animation animation;

    public StaticAbility(Animation animation) {
        this.animation = animation;
    }

    @Override
    public void update(DynamicObject object, int delta) {

    }

    @Override
    public Animation currentAnimation() {
        return animation;
    }

    @Override
    public void collectAnimations(List<Animation> collector) {
        collector.add(animation);
    }
}

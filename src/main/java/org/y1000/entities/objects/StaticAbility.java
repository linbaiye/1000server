package org.y1000.entities.objects;

import java.util.List;

public class StaticAbility implements DynamicAbility {
    @Override
    public void update(DynamicObject object, int delta) {

    }

    @Override
    public Animation currentAnimation() {
        return null;
    }

    @Override
    public void collectAnimations(List<Animation> collector) {

    }
}

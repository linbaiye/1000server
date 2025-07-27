package org.y1000.entities.objects;

import java.util.List;

public class DynamicObjectRespawnAbility implements DynamicAbility {
    private int respawnMillis;

    public DynamicObjectRespawnAbility(int respawnMillis) {
        this.respawnMillis = respawnMillis;
    }

    @Override
    public void update(DynamicObject object, int delta) {
        respawnMillis -= delta;
        if (respawnMillis <= 0) {

        }
    }

    @Override
    public Animation currentAnimation() {
        return null;
    }

    @Override
    public void collectAnimations(List<Animation> collector) {
    }
}

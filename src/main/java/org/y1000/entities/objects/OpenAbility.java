package org.y1000.entities.objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.util.List;

@Slf4j
public class OpenAbility implements DynamicAbility {

    private int openMillis;

    private final Animation[] animations;

    private int current;

    private final String sound;

    private final boolean removal;

    private final int respawnMillis;

    public OpenAbility(int openMillis,
                       List<Animation> animationList, String sound, boolean removal,
                       int respawnMillis) {
        Validate.isTrue(animationList.size() <= 2);
        this.openMillis = openMillis;
        this.animations = animationList.toArray(new Animation[0]);
        this.sound = sound;
        this.removal = removal;
        current = 0;
        this.respawnMillis = respawnMillis;
    }

    public void update(DynamicObject dynamicObject, int delta) {
        boolean donePlay = animations[current].elapse(delta);
        if (current == 0) {
            if (!donePlay)
                return;
            if (removal)
                dynamicObject.free();
            if (animations.length == 2) {
                current++;
            }
        }
        openMillis -= delta;
        if (openMillis > 0)
            return;
        if (removal)
            dynamicObject.sendEvent(DynamicObjectRemoveEvent.of(dynamicObject, respawnMillis));
        else
            dynamicObject.sendEvent(new DynamicObjectRespawnEvent(dynamicObject));
    }

    public void triggered(DynamicObject object) {
        if (sound != null)
            object.sendEvent(DynamicObjectSoundEvent.of(object, sound));
        if (animations.length == 1)
            object.sendEvent(DynamicObjectShiftEvent.of(object, animations[0].getId(), removal));
        else
            object.sendEvent(DynamicObjectShiftEvent.of(object, animations[0].getId(), animations[1].getId(), removal));
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

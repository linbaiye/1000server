package org.y1000.entities.objects;

import java.util.List;

interface DynamicAbility {

    void update(DynamicObject object, int delta);

    Animation currentAnimation();

    void collectAnimations(List<Animation> collector);


}

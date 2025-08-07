package org.y1000.entities.objects;

import lombok.Setter;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.DynamicObjectDropItemAbility;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Damage;

import java.util.List;
import java.util.function.BiConsumer;

public class DynamicObjectHurtAbility implements HurtAbility {

    private final int maxLife;

    private int currentLife;

    private final String sound;

    private final String dieSound;

    @Setter
    private BiConsumer<ActiveEntity, ? super DynamicObjectHurtAbility> onHurt;

    private final List<String> callNpc;

    public DynamicObjectHurtAbility(int maxLife, String sound, String dieSound,
                                    List<String> callNpc) {
        this.maxLife = maxLife;
        currentLife = maxLife;
        this.sound = sound;
        this.dieSound = dieSound;
        this.callNpc = callNpc;
    }

    @Override
    public boolean canBeAttacked() {
        return currentLife() > 0;
    }

    @Override
    public boolean swingAllowed() {
        return false;
    }

    @Override
    public int attacked(ActiveEntity attacker, Damage damage, int accuracy) {
        if (isDead())
            return -1;
        currentLife -= damage.bodyDamage();
        if (currentLife < 0)
            currentLife = 0;
        onHurt.accept(attacker, this);
        return -1;
    }

    public boolean isDead() {
        return currentLife == 0;
    }

    public void apply(DynamicObject dynamicObject, ActiveEntity attacker) {
        if (maxLife > 1)
            dynamicObject.sendEvent(DynamicObjectLifeBarEvent.of(dynamicObject, currentLife, maxLife));
        if (isDead() && dieSound != null)
            dynamicObject.sendEvent(DynamicObjectSoundEvent.of(dynamicObject, dieSound));
        else if (sound != null)
            dynamicObject.sendEvent(DynamicObjectSoundEvent.of(dynamicObject, sound));
        if (!callNpc.isEmpty()) {
            dynamicObject.sendEvent(new DynamicObjectCallNpcEvent(dynamicObject, attacker, callNpc));
            callNpc.clear();
        }
        if (isDead())
            dynamicObject.findAbility(DynamicObjectDropItemAbility.class)
                    .ifPresent(d -> d.apply(dynamicObject));
    }

    @Override
    public int currentLife() {
        return currentLife;
    }

    @Override
    public int maxLife() {
        return maxLife;
    }
}

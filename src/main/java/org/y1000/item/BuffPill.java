package org.y1000.item;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Damage;

@Getter
public final class BuffPill extends AbstractItem {

    private final Damage damage;

    private final int duration;

    private final int iconId;

    public BuffPill(String name,
                    String dropSound,
                    String eventSound,
                    String description,
                    int bodyDamage,
                    int duration, int iconId) {
        super(name, ItemType.BUFF_PILL, dropSound, eventSound, description);
        this.iconId = iconId;
        Validate.isTrue(duration >= 1000);
        this.damage = new Damage(bodyDamage, 0, 0, 0);
        this.duration = duration;
    }

    public int getLastingSeconds() {
        return duration / 1000;
    }
}

package org.y1000.message.serverevent;

import org.y1000.entities.Entity;

public final class CreatureAttackEvent implements EntityEvent {

    public CreatureAttackEvent(Entity source) {
    }


    @Override
    public Entity source() {
        return null;
    }

    @Override
    public void accept(EntityEventHandler visitor) {

    }
}

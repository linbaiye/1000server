package org.y1000.entities;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.message.serverevent.EntityEventListener;

public interface PhysicalEntity extends Entity {
    /**
     * Return true if we can be hit.
     * @return true/false.
     */
    default boolean attackable() {
        return true;
    }

    default void attackedBy(ViolentCreature attacker) {

    }

    default void attackedBy(Projectile projectile) {

    }

    default boolean canAttack(PhysicalEntity target) {
        return target != null &&
                target.canBeSeenAt(coordinate()) &&
                target.attackable();
    }

    void registerOrderedEventListener(EntityEventListener listener);

    void deregisterEventListener(EntityEventListener listener);
}

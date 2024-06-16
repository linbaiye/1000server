package org.y1000.entities;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEventListener;

public interface PhysicalEntity extends Entity {
    /**
     * Return true if we can be hit.
     * @return true/false.
     */
    default boolean attackable() {
        return true;
    }

    default boolean attackedBy(ViolentCreature attacker) {
        return false;
    }

    default boolean attackedBy(Player attacker) {
        return false;
    }

    default void attackedBy(Projectile projectile) {

    }

    default boolean canAttack(PhysicalEntity target) {
        return target != null &&
                target.canBeSeenAt(coordinate()) &&
                target.attackable();
    }

    void registerEventListener(EntityEventListener listener);

    void deregisterEventListener(EntityEventListener listener);
}

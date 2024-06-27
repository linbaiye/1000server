package org.y1000.entities;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.realm.RealmMap;

public interface AttackableEntity extends Entity {
    /**
     * Return true if we can be hit.
     * @return true/false.
     */
    boolean canBeAttackedNow();

    default boolean attackedBy(ViolentCreature attacker) {
        return false;
    }

    default boolean attackedBy(Player attacker) {
        return false;
    }

    default void attackedBy(Projectile projectile) {

    }

    RealmMap realmMap();

    default boolean canPurchaseOrAttack(Entity target) {
        return target instanceof AttackableEntity attackableEntity &&
                target.canBeSeenAt(coordinate()) &&
                attackableEntity.canBeAttackedNow();
    }

}

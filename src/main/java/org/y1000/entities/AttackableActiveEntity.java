package org.y1000.entities;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface AttackableActiveEntity extends ActiveEntity {
    /**
     * Return true if we can be hit.
     * @return true/false.
     */
    boolean canBeAttackedNow();

    boolean attackedBy(Player attacker);

    void attackedBy(ViolentCreature attacker);

    void attackedBy(Projectile projectile);

    RealmMap realmMap();

    default boolean canChaseOrAttack(Entity target) {
        return target instanceof AttackableActiveEntity attackableEntity &&
                attackableEntity.realmMap() == realmMap() &&
                target.canBeSeenAt(coordinate()) &&
                attackableEntity.canBeAttackedNow();
    }

    default boolean canBeMeleeAt(Coordinate coordinate) {
        return coordinate != null && coordinate().directDistance(coordinate) < 2;
    }
}

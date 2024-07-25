package org.y1000.entities.objects;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

public class KillableDynamicObject extends AbstractMutableDynamicObject {

    private final int armor;

    private int life;

    public KillableDynamicObject(long id, Coordinate coordinate,
                                 RealmMap realmMap,
                                 DynamicObjectSdb dynamicObjectSdb, String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName);
        this.armor = dynamicObjectSdb.getArmor(idName);
    }

    @Override
    protected void onAnimationDone() {

    }


    @Override
    public boolean attackedBy(Player attacker) {
        return false;
    }

    @Override
    public void attackedBy(ViolentCreature attacker) {

    }

    @Override
    public void attackedBy(Projectile projectile) {

    }

    public void attackedByAoe(Damage damage) {
        if (life <= 0) {
            return;
        }
        life -= Math.min(damage.bodyDamage() - armor, life);
        if (life == 0) {

        }
    }


    @Override
    public void update(int delta) {

    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return null;
    }

    @Override
    public DynamicObjectType type() {
        return null;
    }
}

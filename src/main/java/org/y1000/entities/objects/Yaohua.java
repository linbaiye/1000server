package org.y1000.entities.objects;

import lombok.Builder;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

public final class Yaohua extends AbstractMutableDynamicObject {

    @Builder
    public Yaohua(long id, Coordinate coordinate, RealmMap realmMap,
                  DynamicObjectSdb dynamicObjectSdb, String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName);
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

    @Override
    public void update(int delta) {

    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return new DynamicObjectInterpolation(this, animationElapsedDuration());
    }

    @Override
    public DynamicObjectType type() {
        return DynamicObjectType.YAOHUA;
    }
}

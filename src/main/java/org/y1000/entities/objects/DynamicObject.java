package org.y1000.entities.objects;

import org.y1000.entities.AbstractEntity;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.Projectile;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;


public final class DynamicObject extends AbstractEntity implements AttackableEntity {

    private final Coordinate coordinate;

    public DynamicObject(long id,
                         Coordinate coordinate) {
        super(id);
        this.coordinate = coordinate;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public void update(int delta) {

    }

    @Override
    public AbstractEntityInterpolation captureInterpolation() {
        return null;
    }

    @Override
    public boolean canBeAttackedNow() {
        return true;
    }


    @Override
    public boolean attackedBy(Player attacker) {
        return false;
    }



    @Override
    public RealmMap realmMap() {
        return null;
    }

}

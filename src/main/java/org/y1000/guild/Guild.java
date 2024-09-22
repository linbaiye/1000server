package org.y1000.guild;

import org.y1000.entities.objects.AbstractKillableDynamicObject;
import org.y1000.entities.objects.Animation;
import org.y1000.entities.objects.DynamicObjectType;
import org.y1000.entities.players.Damage;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

public final class Guild extends AbstractKillableDynamicObject  {

    private Long id;

    private String name;

    private int currentHealth;

    private int maxHealth;

    public Guild(long id, Coordinate coordinate, RealmMap realmMap, DynamicObjectSdb dynamicObjectSdb, String idName, Animation[] animations) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName, animations);
    }

    @Override
    protected void handleDamaged(Damage damage) {

    }

    @Override
    protected void onAnimationDone() {

    }

    @Override
    public DynamicObjectType type() {
        return null;
    }
}

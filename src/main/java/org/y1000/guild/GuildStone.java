package org.y1000.guild;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.Remove;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.objects.*;
import org.y1000.entities.players.Damage;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;

@Getter
public final class GuildStone extends AbstractKillableDynamicObject  {

    private final int realmId;

    private Integer persistentId;

    private int nextHealthTime;

    public void setPersistentId(int persistentId) {
        if (this.persistentId != null)
            throw new IllegalStateException();
        this.persistentId = persistentId;
    }

    @Builder
    public GuildStone(long id,
                      Coordinate coordinate,
                      RealmMap realmMap,
                      DynamicObjectSdb dynamicObjectSdb,
                      int currentHealth,
                      String idName,
                      int realmId,
                      Integer persistentId) {
        super(id, coordinate, realmMap, dynamicObjectSdb, currentHealth, idName, new Animation[]{new Animation(0, 0, false)});
        this.realmId = realmId;
        this.persistentId = persistentId;
        nextHealthTime = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildStone that = (GuildStone) o;
        return Objects.equals(persistentId, that.persistentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persistentId);
    }

    @Override
    public DynamicObjectType type() {
        return DynamicObjectType.GUILD_STONE;
    }

    @Override
    protected void handleDamaged(Damage damage) {
        damageLife(damage);
        if (currentLife() <= 0) {
            realmMap().free(this);
            emitEvent(new DynamicObjectDieEvent(this));
        }
    }

    @Override
    protected void onAnimationDone() {
    }

    @Override
    public void update(int delta) {
        if (nextHealthTime > 0)
            nextHealthTime -= delta;
    }
}

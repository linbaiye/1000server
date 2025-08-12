package org.y1000.guild;

import lombok.Builder;
import lombok.Getter;
import org.y1000.entities.AbstractActiveEntity;
import org.y1000.entities.objects.*;
import org.y1000.entities.players.Damage;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;
import java.util.Optional;

@Getter
public final class GuildStone extends AbstractActiveEntity {

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
        super(id);
        //super(id, coordinate, realmMap, dynamicObjectSdb, currentHealth, idName, new IAnimation[]{new IAnimation(0, 0, false)});
        this.realmId = realmId;
        this.persistentId = persistentId;
        nextHealthTime = 0;
    }

    public String idName() {
        return "";
    }

    public int getMaxLife() {
        return 0;
    }
    public int currentLife() {
        return 0;
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
    public void update(int delta) {

    }

    @Override
    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return Optional.empty();
    }

    @Override
    public Coordinate coordinate() {
        return null;
    }

    @Override
    public I2ClientMessage captureSnapshot() {
        return null;
    }

//    @Override
//    public DynamicObjectType type() {
//        return DynamicObjectType.GUILD_STONE;
//    }
//
//    @Override
//    protected void handleDamaged(Damage damage) {
//        damageLife(damage);
//        if (currentLife() <= 0) {
//            realmMap().free(this);
//            emitEvent(new DynamicObjectDieEvent(this));
//        }
//    }
//
//    @Override
//    protected void onAnimationDone() {
//    }
//
//    @Override
//    public void update(int delta) {
//        if (nextHealthTime > 0)
//            nextHealthTime -= delta;
//    }
//
//    @Override
//    public <AB> Optional<AB> findAbility(Class<AB> type) {
//        return Optional.empty();
//    }
}

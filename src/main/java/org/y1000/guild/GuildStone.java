package org.y1000.guild;

import lombok.Builder;
import lombok.Getter;
import org.y1000.entities.objects.AbstractSimpleKillableDynamicObject;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;

@Getter
public final class GuildStone extends AbstractSimpleKillableDynamicObject {

    private final int realmId;

    private Integer persistentId;

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
        super(id, coordinate, realmMap, dynamicObjectSdb, currentHealth, idName);
        this.realmId = realmId;
        this.persistentId = persistentId;
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
}

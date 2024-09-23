package org.y1000.guild;

import lombok.Builder;
import org.y1000.entities.objects.AbstractSimpleKillableDynamicObject;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

public final class GuildStone extends AbstractSimpleKillableDynamicObject {

    @Builder
    public GuildStone(long id,
                      Coordinate coordinate,
                      RealmMap realmMap,
                      DynamicObjectSdb dynamicObjectSdb,
                      int currentHealth,
                      String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, currentHealth, idName);
    }

}

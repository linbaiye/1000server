package org.y1000.entities.creatures.npc;

import org.y1000.realm.RealmMap;
import org.y1000.sdb.CreateNonMonsterSdb;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

public interface NpcFactory {

    Npc create(long id, String idName, RealmMap realmMap, Coordinate coordinate, NpcEventListener listener);

}

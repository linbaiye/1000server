package org.y1000.entities.creatures.npc;

import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface NpcFactory {

    NpcImpl create(long id, String idName, RealmMap realmMap, Coordinate coordinate, NpcEventListener listener);

    NpcImpl createCalled(long id, String idName, RealmMap realmMap, Coordinate coordinate, NpcEventListener listener);

}

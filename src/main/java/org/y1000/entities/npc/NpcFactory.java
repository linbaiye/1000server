package org.y1000.entities.npc;

import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface NpcFactory {

    NpcImpl create(long id, String idName, RealmMap realmMap, Coordinate coordinate, NpcEventListener listener);

    NpcImpl createCopied(long id, String idName, RealmMap realmMap, Coordinate coordinate, NpcEventListener listener);

    NpcImpl createCalledNpc(long id, String idName, RealmMap realmMap, Coordinate coordinate, NpcEventListener listener);

}

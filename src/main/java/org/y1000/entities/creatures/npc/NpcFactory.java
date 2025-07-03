package org.y1000.entities.creatures.npc;

import org.y1000.realm.RealmMap;
import org.y1000.sdb.CreateNonMonsterSdb;
import org.y1000.util.Coordinate;

public interface NpcFactory {

    INpc createNpc(String name, long id, RealmMap realmMap, Coordinate coordinate);

    INpc createClonedNpc(INpc npc, long id, Coordinate coordinate);

    INpc createNonMonsterNpc(String name, long id, RealmMap realmMap, Coordinate coordinate, CreateNonMonsterSdb createNonMonsterSdb);

}

package org.y1000.entities.creatures.npc;

import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface NpcFactory {


    Npc createMerchant(String name, long id, RealmMap realmMap, Coordinate coordinate);

    Npc createNpc(String name, long id, RealmMap realmMap, Coordinate coordinate);

    Npc createClonedNpc(Npc npc, long id, Coordinate coordinate);

}

package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.monster.Monster;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface NpcFactory {

    Monster createMonster(String name, long id, RealmMap realmMap, Coordinate coordinate);


    Npc createMerchant(String name, long id, RealmMap realmMap, Coordinate coordinate);

}

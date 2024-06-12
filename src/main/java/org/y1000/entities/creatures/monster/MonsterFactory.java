package org.y1000.entities.creatures.monster;

import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Collections;

public interface MonsterFactory {

    AbstractMonster createMonster(String name, long id, RealmMap realmMap, Coordinate coordinate);

}

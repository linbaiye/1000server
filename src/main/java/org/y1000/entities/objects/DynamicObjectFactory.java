package org.y1000.entities.objects;

import org.y1000.guild.Guild;
import org.y1000.realm.DynamicObjectEventListener;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.CreateDynamicObjectSdb;
import org.y1000.util.Coordinate;

public interface DynamicObjectFactory {


    Guild createGuildStone(long id, String name, int realmId, RealmMap realmMap, Coordinate coordinate);

    String checkCreateGuildStone(String name);

    DynamicObject create(long id, String number, DynamicObjectEventListener listener, CreateDynamicObjectSdb createDynamicObjectSdb);

}

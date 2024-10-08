package org.y1000.entities.objects;

import org.y1000.guild.GuildStone;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface DynamicObjectFactory {

    DynamicObject createDynamicObject(String name, long id, RealmMap realmMap, Coordinate coordinate);

    GuildStone createGuildStone(long id, String name, int realmId, RealmMap realmMap, Coordinate coordinate);

    String checkCreateGuildStone(String name);

}

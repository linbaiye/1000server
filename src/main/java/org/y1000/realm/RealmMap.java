package org.y1000.realm;


import org.y1000.entities.Entity;
import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.teleport.Teleport;
import org.y1000.util.Coordinate;

import java.util.Optional;

public interface RealmMap {

    boolean movable(Coordinate coordinate);
    boolean tileMovable(Coordinate coordinate);

    void occupy(Entity entity);

    void free(Entity creature);

    void occupy(DynamicObject dynamicObject);

    void free(DynamicObject dynamicObject);

    String mapFile();

    default String tileFile() {
        return "";
    }

    default String objectFile() {
        return "";
    }
    default String roofFile() {
        return "";
    }

    static Optional<RealmMap> Load(String name) {
        return RealmMapV2Impl.read(name, "", "", "");
    }

    static Optional<RealmMap> Load(String name, String tilename, String objName, String rofName) {
        return RealmMapV2Impl.read(name, tilename, objName, rofName);
    }

    void addTeleport(Teleport teleport);
}

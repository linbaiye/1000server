package org.y1000.realm;

import org.y1000.entities.ActiveEntity;
import org.y1000.util.Coordinate;

public interface NpcCaller {

    void call(String name, ActiveEntity enemy, Coordinate callAt);
}

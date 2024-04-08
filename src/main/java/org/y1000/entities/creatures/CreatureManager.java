package org.y1000.entities.creatures;

import org.y1000.entities.managers.EntityManager;
import org.y1000.util.Coordinate;

public class CreatureManager implements EntityManager<Creature> {
    @Override
    public Creature findOne(Coordinate coordinate) {
        return null;
    }


    @Override
    public void update(long delta) {
    }


    public static CreatureManager ofRealm(String realmName) {
        return null;
    }

}

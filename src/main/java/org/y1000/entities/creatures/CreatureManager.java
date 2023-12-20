package org.y1000.entities.creatures;

import org.y1000.entities.managers.EntityManager;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.Message;
import org.y1000.util.Coordinate;

import java.util.List;

public class CreatureManager implements EntityManager<Creature> {
    @Override
    public Creature findOne(Coordinate coordinate) {
        return null;
    }


    @Override
    public List<I2ClientMessage> update(long delta, long timeMillis) {
        return null;
    }


    public static CreatureManager ofRealm(String realmName) {
        return null;
    }

}

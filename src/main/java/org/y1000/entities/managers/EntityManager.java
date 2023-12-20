package org.y1000.entities.managers;

import org.y1000.entities.Entity;
import org.y1000.message.I2ClientMessage;
import org.y1000.util.Coordinate;

import java.util.List;

public interface EntityManager<T extends Entity> {

    T findOne(Coordinate coordinate);

    List<I2ClientMessage> update(long delta, long timeMillis);
}

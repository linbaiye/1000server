package org.y1000.entities.managers;

import org.y1000.entities.Entity;
import org.y1000.message.Message;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Set;

public interface EntityManager<T extends Entity> {

    T findOne(Coordinate coordinate);

    List<Message> update(long delta);
}
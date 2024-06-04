package org.y1000;

import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestingPlayerEventListener implements EntityEventListener {

    public List<EntityEvent> entityEvents = new ArrayList<>();

    @Override
    public void OnEvent(EntityEvent entityEvent) {
        this.entityEvents.add(entityEvent);
    }

    public <T extends EntityEvent> T dequeue(Class<T> clazz) {
        return !entityEvents.isEmpty() ? clazz.cast(entityEvents.remove(0)) : null;
    }

    public void clearEvents() {
        entityEvents.clear();
    }
}

package org.y1000;

import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestingEventListener implements EntityEventListener {

    private final List<EntityEvent> entityEvents = new ArrayList<>();

    @Override
    public void onEvent(EntityEvent entityEvent) {
        this.entityEvents.add(entityEvent);
    }

    public <T extends EntityEvent> T dequeue(Class<T> clazz) {
        return !entityEvents.isEmpty() ? clazz.cast(entityEvents.remove(0)) : null;
    }

    public int eventSize() {
        return entityEvents.size();
    }

    public <T extends EntityEvent> T removeFirst(Class<T> clazz) {
        Iterator<EntityEvent> iterator = entityEvents.iterator();
        while (iterator.hasNext()) {
            EntityEvent next = iterator.next();
            if (clazz.isAssignableFrom(next.getClass())) {
                iterator.remove();
                return clazz.cast(next);
            }
        }
        return null;
    }

    public void clearEvents() {
        entityEvents.clear();
    }
}

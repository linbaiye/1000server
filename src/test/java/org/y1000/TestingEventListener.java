package org.y1000;

import org.y1000.event.TypedEntityEvent;
import org.y1000.event.IEntityEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestingEventListener {

    private final List<TypedEntityEvent> entityEvents = new ArrayList<>();


    public <T extends IEntityEvent> T dequeue(Class<T> clazz) {
        return !entityEvents.isEmpty() ? clazz.cast(entityEvents.remove(0)) : null;
    }

    public int eventSize() {
        return entityEvents.size();
    }

    public boolean isEmpty() {
        return entityEvents.isEmpty();
    }

    public <T extends IEntityEvent> T removeFirst(Class<T> clazz) {
        Iterator<TypedEntityEvent> iterator = entityEvents.iterator();
        while (iterator.hasNext()) {
            TypedEntityEvent next = iterator.next();
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

    @Override
    public void onEvent(TypedEntityEvent entityEvent) {

    }
}

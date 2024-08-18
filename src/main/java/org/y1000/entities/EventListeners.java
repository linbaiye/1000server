package org.y1000.entities;

import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;

import java.util.*;

public final class EventListeners {

    private final Set<EntityEventListener> listeners;

    public EventListeners() {
        listeners = new HashSet<>();
    }

    public void notifyListeners(EntityEvent event) {
        Objects.requireNonNull(event);
        List<EntityEventListener> listenerList = new ArrayList<>(listeners);
        listenerList.forEach(listener -> listener.onEvent(event));
    }

    public void register(EntityEventListener listener) {
        Objects.requireNonNull(listener);
        listeners.add(listener);
    }

    public void clear() {
        listeners.clear();
    }

    public void deregister(EntityEventListener listener) {
        Objects.requireNonNull(listener);
        listeners.remove(listener);
    }
}

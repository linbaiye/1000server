package org.y1000;

import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EventListeners {

    private final List<EntityEventListener> listeners;

    public boolean iterating;

    private final List<EntityEventListener> registering;

    private final List<EntityEventListener> deregistering;

    public EventListeners() {
        listeners = new ArrayList<>();
        registering = new ArrayList<>();
        deregistering = new ArrayList<>();
    }

    public void notifyListeners(EntityEvent event) {
        Objects.requireNonNull(event);
        iterating = true;
        listeners.forEach(listener -> listener.onEvent(event));
        iterating = false;
        if (!deregistering.isEmpty()) {
            listeners.removeAll(deregistering);
        }
        if (!registering.isEmpty()) {
            listeners.addAll(registering);
        }
    }

    public void register(EntityEventListener listener) {
        Objects.requireNonNull(listener);
        if (!iterating) {
            listeners.add(listener);
        } else {
            registering.add(listener);
        }
    }

    public void deregister(EntityEventListener listener) {
        Objects.requireNonNull(listener);
        if (!iterating) {
            listeners.remove(listener);
        } else {
            deregistering.add(listener);
        }
    }
}

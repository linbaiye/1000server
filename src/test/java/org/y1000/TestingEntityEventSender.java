package org.y1000;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.network.Connection;
import org.y1000.realm.EntityEventSender;

import java.util.HashSet;
import java.util.Set;

public final class TestingEntityEventSender implements EntityEventSender {
    private final TestingEventListener eventListener;

    private final Set<Entity> entities;

    public TestingEntityEventSender() {
        this.eventListener = new TestingEventListener();
        this.entities = new HashSet<>();
    }

    @Override
    public void add(Player player, Connection connection) {

    }

    @Override
    public boolean contains(Player player) {
        return false;
    }

    @Override
    public void remove(Player player) {

    }

    @Override
    public void add(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void remove(Entity entity) {
        entities.remove(entity);
    }

    public Set<Entity> entities() {
        return entities;
    }


    public <T extends EntityEvent> T dequeue(Class<T> clazz) {
        return eventListener.dequeue(clazz);
    }

    public int eventSize() {
        return eventListener.eventSize();
    }

    public <T extends EntityEvent> T removeFirst(Class<T> clazz) {
        return eventListener.removeFirst(clazz);
    }

    public void clearEvents() {
        eventListener.clearEvents();
    }

    @Override
    public void sendEvent(EntityEvent entityEvent) {
        eventListener.onEvent(entityEvent);
    }
}

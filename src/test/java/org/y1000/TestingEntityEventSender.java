package org.y1000;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.network.Connection;
import org.y1000.realm.EntityEventSender;

import java.util.HashSet;
import java.util.Set;

public final class TestingEntityEventSender implements EntityEventSender {
    private final TestingEventListener eventListener;

    private final Set<PhysicalEntity> entities;

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
    public void add(PhysicalEntity entity) {
        entities.add(entity);
    }

    @Override
    public void remove(PhysicalEntity entity) {
        entities.remove(entity);
    }

    public Set<PhysicalEntity> entities() {
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

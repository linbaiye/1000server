package org.y1000;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.event.IEntityEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.Connection;
import org.y1000.realm.EntityEventSender;

import java.util.*;

public final class TestingEntityEventSender implements EntityEventSender {
    private final TestingEventListener eventListener;

    private final Set<Entity> entities;

    private final Map<Entity, List<I2ClientMessage>> entityMessages;

    private final Map<Player, Connection> connectionMap;

    public TestingEntityEventSender() {
        this.eventListener = new TestingEventListener();
        this.entities = new HashSet<>();
        entityMessages = new HashMap<>();
        connectionMap = new HashMap<>();
    }

    public void add(Player player, Connection connection) {
        connectionMap.put(player, connection);
        entities.add(player);
    }

    public boolean contains(Player player) {
        return false;
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


    public <T extends IEntityEvent> T dequeue(Class<T> clazz) {
        return eventListener.dequeue(clazz);
    }

    public int eventSize() {
        return eventListener.eventSize();
    }

    public <T extends IEntityEvent> T removeFirst(Class<T> clazz) {
        return eventListener.removeFirst(clazz);
    }

    public void clearEvents() {
        eventListener.clearEvents();
    }

    public <T extends ActiveEntity> T getEntity(Class<T> clazz) {
        return entities.stream()
                .filter(entity -> entity.getClass().isAssignableFrom(clazz))
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }


    @Override
    public void sendEvent(IEntityEvent entityEvent) {
        eventListener.onEvent(entityEvent);
    }

    public <T extends I2ClientMessage> T removeFirst(Entity source, Class<T> clazz) {
        List<I2ClientMessage> messages = entityMessages.get(source);
        Iterator<I2ClientMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            I2ClientMessage next = iterator.next();
            if (clazz.isAssignableFrom(next.getClass())) {
                iterator.remove();
                return clazz.cast(next);
            }
        }
        return null;
    }


    @Override
    public void notifyVisiblePlayers(Entity source, I2ClientMessage serverMessage) {
        entityMessages.putIfAbsent(source, new ArrayList<>());
        entityMessages.get(source).add(serverMessage);
    }

    @Override
    public void notifyVisiblePlayersAndSelf(Entity source, I2ClientMessage serverMessage) {

    }

    @Override
    public void sendTo(Player player, I2ClientMessage message) {

    }
}

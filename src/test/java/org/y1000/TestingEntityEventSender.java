package org.y1000;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.message.ServerMessage;
import org.y1000.network.Connection;
import org.y1000.realm.EntityEventSender;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TestingEntityEventSender implements EntityEventSender {
    private final TestingEventListener eventListener;

    private final Set<Entity> entities;

    private final Map<Entity, List<ServerMessage>> entityMessages;

    private final Map<Player, Connection> connectionMap;

    public TestingEntityEventSender() {
        this.eventListener = new TestingEventListener();
        this.entities = new HashSet<>();
        entityMessages = new HashMap<>();
        connectionMap = new HashMap<>();
    }

    @Override
    public void add(Player player, Connection connection) {
        connectionMap.put(player, connection);
        entities.add(player);
    }

    @Override
    public boolean contains(Player player) {
        return false;
    }

    @Override
    public void remove(Player player) {
        connectionMap.remove(player);
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

    public <T extends Entity> T getEntity(Class<T> clazz) {
        return entities.stream()
                .filter(entity -> entity.getClass().isAssignableFrom(clazz))
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }


    @Override
    public void sendEvent(EntityEvent entityEvent) {
        eventListener.onEvent(entityEvent);
    }

    public <T extends ServerMessage> T removeFirst(Entity source, Class<T> clazz) {
        List<ServerMessage> messages = entityMessages.get(source);
        Iterator<ServerMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            ServerMessage next = iterator.next();
            if (clazz.isAssignableFrom(next.getClass())) {
                iterator.remove();
                return clazz.cast(next);
            }
        }
        return null;
    }


    @Override
    public void notifyVisiblePlayers(Entity source, ServerMessage serverMessage) {
        entityMessages.putIfAbsent(source, new ArrayList<>());
        entityMessages.get(source).add(serverMessage);
    }

    @Override
    public void notifyVisiblePlayersAndSelf(Entity source, ServerMessage serverMessage) {

    }
}

package org.y1000.realm;

import org.slf4j.Logger;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.I2ClientMessage;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractActiveEntityManager<T extends ActiveEntity> implements ActiveEntityManager<T>, EntityEventHandler {
    private boolean iterating;
    private final Set<T> entities;

    private final Set<T> adding;

    private final Set<T> deleting;

    private final AOIManager aoiManager;

    private final MessageSender messageSender;

    protected AbstractActiveEntityManager(AOIManager aoiManager,
                                          MessageSender messageSender) {
        this.aoiManager = aoiManager;
        this.messageSender = messageSender;
        this.iterating = false;
        this.entities = new HashSet<>();
        this.adding = new HashSet<>();
        this.deleting = new HashSet<>();
    }

    protected abstract Logger log();

    private void doUpdate(T t, long delta) {
        try {
            t.update((int)delta);
        } catch (Exception e) {
            log().error("Failed to update {}.", t, e);
        }
    }


    protected void updateManagedEntities(long delta) {
        iterating = true;
        entities.forEach(e -> doUpdate(e, delta));
        iterating = false;
        handleDeleting();
        handleAdding();
    }

    Set<T> getEntities() {
        return entities;
    }


    private void handleAdding() {
        adding.forEach(this::doAdd);
        adding.clear();
    }

    private void handleDeleting() {
        deleting.forEach(this::doDelete);
        deleting.clear();
    }

    private void doAdd(T entity) {
        try {
            entities.add(entity);
        } catch (Exception e) {
            log().error("Exception after adding {}.", entity, e);
        }
    }

    protected AOIManager getAoiManager() {
        return aoiManager;
    }

    private void doDelete(T entity) {
        try {
            entities.remove(entity);
        } catch (Exception e) {
            log().error("Exception after deleting {}.", entity, e);
        }
    }



    void add(T entity) {
        if (entities.contains(entity))
            return;
        if (iterating) {
            deleting.remove(entity);
            adding.add(entity);
        } else {
            doAdd(entity);
        }
        aoiManager.add(entity);
    }

    public boolean contains(T entity) {
        return !deleting.contains(entity) && entities.contains(entity);
    }


    @Override
    public Optional<T> find(long id) {
        return entities.stream()
                .filter(e -> e.id() == id)
                .findFirst();
    }

    @Override
    public Set<T> find(Predicate<? super T> predicate) {
        return entities.stream().filter(predicate).collect(Collectors.toSet());
    }


    protected MessageSender getMessageSender() {
        return messageSender;
    }

    public void sendToVisiblePlayers(Entity source, I2ClientMessage message) {
        aoiManager.filterVisibleEntities(source, Player.class)
                .forEach(p -> messageSender.sendTo(p, message));
    }


    void remove(T entity) {
        if (!entities.contains(entity))
            return;
        if (iterating) {
            adding.remove(entity);
            deleting.add(entity);
        } else {
            doDelete(entity);
        }
        aoiManager.remove(entity);
    }
}

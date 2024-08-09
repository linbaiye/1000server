package org.y1000.realm;

import org.slf4j.Logger;
import org.y1000.entities.ActiveEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractActiveEntityManager<T extends ActiveEntity> implements ActiveEntityManager<T> {
    private boolean iterating;
    private final Set<T> entities;

    private final Set<T> adding;

    private final Set<T> deleting;

    protected AbstractActiveEntityManager() {
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

    private void doDelete(T entity) {
        try {
            entities.remove(entity);
        } catch (Exception e) {
            log().error("Exception after deleting {}.", entity, e);
        }
    }

    void add(T entity) {
        if (iterating) {
            deleting.remove(entity);
            adding.add(entity);
        } else {
            doAdd(entity);
        }
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


    void remove(T entity) {
        if (iterating) {
            adding.remove(entity);
            deleting.add(entity);
        } else {
            doDelete(entity);
        }
    }
}

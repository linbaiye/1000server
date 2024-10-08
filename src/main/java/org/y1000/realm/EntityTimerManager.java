package org.y1000.realm;

import org.y1000.entities.ActiveEntity;

import java.util.*;

final class EntityTimerManager<E extends ActiveEntity>  {
    private final List<EntityTimer<E>> entityTimers;

    public EntityTimerManager() {
        entityTimers = new ArrayList<>();
    }

    public void add(E entity, int millis) {
        if (entity != null && millis >= 0 && !contains(entity))
            entityTimers.add(new EntityTimer<>(entity, millis));
    }

    private boolean contains(E e) {
        if (e == null) {
            return false;
        }
        return entityTimers.stream().anyMatch(timer -> timer.e.equals(e));
    }

    /**
     * Update and return entities that are ready to respawn.
     * @param delta
     * @return
     */
    public Set<E> update(long delta) {
        Set<E> entities = null;
        Iterator<EntityTimer<E>> iterator = entityTimers.iterator();
        while (iterator.hasNext()) {
            EntityTimer<E> next = iterator.next();
            if (next.update(delta).timeUp()) {
                iterator.remove();
                if (entities == null) {
                    entities = new HashSet<>();
                }
                entities.add(next.getEntity());
            }
        }
        return entities == null ? Collections.emptySet() : entities;
    }

    private static class EntityTimer<E extends ActiveEntity> {
        private final E e;
        private int time;

        EntityTimer(E e, int time) {
            this.e = e ;
            this.time = time;
        }

        public EntityTimer<E> update(long delta) {
            this.time -= (int) delta;
            return this;
        }

        public boolean timeUp() {
            return this.time <= 0;
        }

        public E getEntity() {
            return e;
        }
    }
}

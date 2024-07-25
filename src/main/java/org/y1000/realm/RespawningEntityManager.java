package org.y1000.realm;

import org.y1000.entities.Entity;

import java.util.*;

final class RespawningEntityManager<E extends Entity>  {
    private final List<RespawningEntity<E>> respawningEntities;

    public RespawningEntityManager() {
        respawningEntities = new ArrayList<>();
    }

    public void add(E entity, int millis) {
        if (entity != null && millis > 0)
            respawningEntities.add(new RespawningEntity<>(entity, millis));
    }

    /**
     * Update and return entities that are ready to respawn.
     * @param delta
     * @return
     */
    public Set<E> update(long delta) {
        Set<E> entities = null;
        Iterator<RespawningEntity<E>> iterator = respawningEntities.iterator();
        while (iterator.hasNext()) {
            RespawningEntity<E> next = iterator.next();
            if (next.update(delta).canRespawn()) {
                iterator.remove();
                if (entities == null) {
                    entities = new HashSet<>();
                }
                entities.add(next.getEntity());
            }
        }
        return entities == null ? Collections.emptySet() : entities;
    }

    private static class RespawningEntity<E extends Entity> {
        private final E e;
        private int time;

        RespawningEntity(E e, int time) {
            this.e = e ;
            this.time = time;
        }

        public RespawningEntity<E> update(long delta) {
            this.time -= (int) delta;
            return this;
        }

        public boolean canRespawn() {
            return this.time <= 0;
        }

        public E getEntity() {
            return e;
        }
    }
}

package org.y1000.realm;

import org.y1000.entities.Entity;

public class RespawningEntity<E extends Entity> {
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

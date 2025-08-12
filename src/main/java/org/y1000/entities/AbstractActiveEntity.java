package org.y1000.entities;


public abstract class AbstractActiveEntity implements ActiveEntity {

    private final long id;

    protected AbstractActiveEntity(long id) {
        this.id = id;
    }


    @Override
    public long id() {
        return id;
    }
}

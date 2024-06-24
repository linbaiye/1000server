package org.y1000.realm;

public final class EntityIdGenerator {
    private long id = 1;

    public long next() {
        return id++;
    }
}

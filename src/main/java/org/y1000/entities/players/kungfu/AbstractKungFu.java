package org.y1000.entities.players.kungfu;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractKungFu implements KungFu {

    private int level;

    private final String name;

    public int level() {
        return level;
    }

    public String name() {
        return name;
    }
}

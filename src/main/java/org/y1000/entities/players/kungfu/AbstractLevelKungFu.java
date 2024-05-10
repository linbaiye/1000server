package org.y1000.entities.players.kungfu;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractLevelKungFu implements LevelKungFu {

    private int level;

    public int level() {
        return level;
    }
}

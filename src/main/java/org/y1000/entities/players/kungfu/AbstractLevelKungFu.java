package org.y1000.entities.players.kungfu;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractLevelKungFu implements LevelKungFu {

    private float level;

    public float level() {
        return level;
    }

}

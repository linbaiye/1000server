package org.y1000.entities.players.kungfu;

public interface FootKungFu extends LevelKungFu {

    default boolean canFly() {
        return level() >= 8501;
    }
}

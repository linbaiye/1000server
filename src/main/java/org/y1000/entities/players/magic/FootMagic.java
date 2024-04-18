package org.y1000.entities.players.magic;

public interface FootMagic {
    float level();


    default boolean canFly() {
        return level() >= 85.01;
    }

    String name();

}

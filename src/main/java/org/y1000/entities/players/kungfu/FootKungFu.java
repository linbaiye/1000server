package org.y1000.entities.players.kungfu;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class FootKungFu extends AbstractKungFu {

    private final String name;

    public boolean canFly() {
        return level() >= 8501;
    }

    @Override
    public String name() {
        return name;
    }

    public static FootKungFu unnamed() {
        return FootKungFu.builder()
                .name("无名步法")
                .level(100)
                .build();
    }
}

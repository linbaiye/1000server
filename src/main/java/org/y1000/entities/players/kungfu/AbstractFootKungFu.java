package org.y1000.entities.players.kungfu;

public abstract class AbstractFootKungFu implements FootKungFu {

    private final String name;
    private float level;

    public AbstractFootKungFu(String name, float level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public float level() {
        return level;
    }
}

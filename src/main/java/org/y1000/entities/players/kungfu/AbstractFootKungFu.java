package org.y1000.entities.players.kungfu;

public abstract class AbstractFootKungFu implements FootKungFu {

    private final String name;
    private int level;

    public AbstractFootKungFu(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int level() {
        return level;
    }
}

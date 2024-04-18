package org.y1000.entities.players.magic;

public abstract class AbstractFootMagic implements FootMagic {

    private final String name;
    private float level;

    public AbstractFootMagic(String name, float level) {
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

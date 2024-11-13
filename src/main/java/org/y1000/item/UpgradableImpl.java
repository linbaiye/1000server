package org.y1000.item;

public final class UpgradableImpl implements Upgradable {

    private int level;

    public UpgradableImpl(int level) {
        this.level = level;
    }

    public UpgradableImpl() {
        this(0);
    }

    @Override
    public int level() {
        return level;
    }

    @Override
    public void upgrade() {
        if (level >= 4)
            throw new IllegalStateException();
        level++;
    }

    @Override
    public float percentage() {
        return switch (level()) {
            case 0 -> 0;
            case 1 -> 0.2f;
            case 2 -> 0.5f;
            case 3 -> 1f;
            case 4 -> 1.5f;
            default -> throw new IllegalStateException();
        };
    }
}

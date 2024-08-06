package org.y1000.entities;

public final class EmptyAttributeProvider implements AttributeProvider {

    public static final EmptyAttributeProvider INSTANCE = new EmptyAttributeProvider();

    private EmptyAttributeProvider() {}
    @Override
    public int life() {
        return 0;
    }

    @Override
    public int avoidance() {
        return 0;
    }

    @Override
    public int recovery() {
        return 0;
    }

    @Override
    public int attackSpeed() {
        return 0;
    }

    @Override
    public int wanderingRange() {
        return 0;
    }

    @Override
    public int armor() {
        return 0;
    }

    @Override
    public int hit() {
        return 0;
    }

    @Override
    public int viewWidth() {
        return 0;
    }


    @Override
    public int damage() {
        return 0;
    }

    @Override
    public String hurtSound() {
        return null;
    }
}

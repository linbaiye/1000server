package org.y1000.item;

public final class DyableImpl implements Dyable {

    private int color;

    public DyableImpl(int color) {
        this.color = color;
    }

    @Override
    public void dye(int color) {
        this.color = color;
    }

    @Override
    public void bleach(int color) {
        this.color += color;
        this.color %= 256;
    }

    @Override
    public int color() {
        return color;
    }

}

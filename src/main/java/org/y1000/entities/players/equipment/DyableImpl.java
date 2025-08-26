package org.y1000.entities.players.equipment;


import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty
    public int color() {
        return color;
    }

    @Override
    public String toString() {
        return "DyableImpl{" +
                "color=" + color +
                '}';
    }

    @Override
    public EquipmentAbilityType abilityType() {
        return EquipmentAbilityType.Dyable;
    }
}

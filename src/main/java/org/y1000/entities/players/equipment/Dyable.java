package org.y1000.entities.players.equipment;

public interface Dyable extends EquipmentAbility {

    void dye(int color);

    void bleach(int color);

    int color();
}

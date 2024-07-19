package org.y1000.item;

import org.y1000.entities.players.Armor;

public interface ArmorEquipment extends SexualEquipment {
    int avoidance();

    Armor armor();

    int recovery();

}

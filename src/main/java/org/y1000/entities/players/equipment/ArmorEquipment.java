package org.y1000.entities.players.equipment;

import org.y1000.entities.players.Armor;
import org.y1000.entities.players.equipment.SexualEquipment;

public interface ArmorEquipment extends SexualEquipment {
    int avoidance();

    Armor armor();

    int recovery();

}

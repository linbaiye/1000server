package org.y1000.message.input;

import org.y1000.item.EquipmentType;

public record ClientUnequipEvent(EquipmentType type) implements ClientEvent {
}

package org.y1000.message.clientevent;

import org.y1000.item.EquipmentType;

public record ClientUnequipEvent(EquipmentType type) implements ClientEvent {
}

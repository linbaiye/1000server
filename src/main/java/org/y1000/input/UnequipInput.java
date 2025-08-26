package org.y1000.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.entities.players.equipment.EquipmentType;

public record UnequipInput(EquipmentType type) implements SelfHandleInput {
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.unequip(type);
    }
}

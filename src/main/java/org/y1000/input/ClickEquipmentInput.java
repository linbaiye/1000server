package org.y1000.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.item.EquipmentType;

public record ClickEquipmentInput(int type) implements SelfHandleInput {
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.clickEquipment(EquipmentType.fromValue(type));
    }
}

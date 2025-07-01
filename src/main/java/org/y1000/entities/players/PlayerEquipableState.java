package org.y1000.entities.players;

import org.y1000.item.Equipment;

interface PlayerEquipableState {
    default void equip(PlayerImpl player, int slot, Equipment equipment) {
        player.tryEquipFromSlot(slot, equipment);
    }
}

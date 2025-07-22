package org.y1000.entities.players;

import org.y1000.item.EquipmentType;
import org.y1000.message.input.*;
import org.y1000.util.Coordinate;

public interface PlayerInputHandler {
    void move(MoveInput moveInput);

    void turn(TurnInput turnInput);

    void handleSimpleInput(SimpleInput.Type type);

    void onKungFuClicked(int page, int slot, ClickKungFuInput.ClickType type);

    void swapItem(int slot1, int slot2);

    void onInventorySlotClicked(int slot, ClickInventorySlotInput.ClickType type);

    void unequip(EquipmentType type);

    void swapKungFu(int page, int slot1, int slot2);

    void startDropItem(int slot, Coordinate at);

    void confirmDropItem(int slot, int number, Coordinate at);

    void changeTradeState(PlayerTradeStateInput.State state);

    void addTradeItem(int slot, int number);

}

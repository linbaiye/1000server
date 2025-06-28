package org.y1000.entities.players;

import org.y1000.message.input.*;

public interface PlayerInputHandler {
    void move(MoveInput moveInput);

    void turn(TurnInput turnInput);

    void handleSimpleInput(SimpleInput.Type type);

    void onKungFuClicked(int page, int slot, ClickKungFuInput.ClickType type);

    void swapItem(int slot1, int slot2);

    void onInventorySlotClicked(int slot, ClickInventorySlotInput.ClickType type);

}

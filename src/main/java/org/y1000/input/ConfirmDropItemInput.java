package org.y1000.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.network.gen.ConfirmDropItemInputPacket;
import org.y1000.util.Coordinate;

public record ConfirmDropItemInput(int slot, int number, Coordinate droppedAt) implements SelfHandleInput {
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.confirmDropItem(slot, number, droppedAt);
    }

    public static ConfirmDropItemInput fromPacket(ConfirmDropItemInputPacket packet) {
        return new ConfirmDropItemInput(packet.getSlot(), packet.getNumber(), Coordinate.xy(packet.getX(), packet.getY()));
    }
}

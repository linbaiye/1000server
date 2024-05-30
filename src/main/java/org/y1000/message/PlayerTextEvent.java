package org.y1000.message;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TextMessagePacket;

public class PlayerTextEvent extends AbstractPlayerEvent {

    private final String text;

    public PlayerTextEvent(Player source, String text) {
        super(source);
        Validate.isTrue(text != null && text.length() <= 30);
        this.text = text;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setText(TextMessagePacket.newBuilder().setText(text))
                .build();
    }

    public static PlayerTextEvent tooFarAway(Player player) {
        return new PlayerTextEvent(player, "距离过远");
    }

    public static PlayerTextEvent inventoryFull(Player player) {
        return new PlayerTextEvent(player, "物品栏已满");
    }

    public static PlayerTextEvent tradeDisabled(Player player) {
        return new PlayerTextEvent(player, "对方拒绝交易");
    }
}

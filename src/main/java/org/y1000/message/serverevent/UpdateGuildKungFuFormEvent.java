package org.y1000.message.serverevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.ValueEnum;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateGuildKungFuFormPacket;

public final class UpdateGuildKungFuFormEvent extends AbstractPlayerEvent {
    private final Packet packet;

    public UpdateGuildKungFuFormEvent(Player source, Command command, String text) {
        super(source);
        this.packet = Packet.newBuilder().setKungFuForm(
                UpdateGuildKungFuFormPacket.newBuilder()
                        .setCommand(command.value())
                        .setText(text)
                        .build()
        ).build();
    }

    public enum Command implements ValueEnum {
        OPEN(1),
        TEXT(2),
        CLOSE(3),
        ;

        private final int v;

        Command(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }

    public static UpdateGuildKungFuFormEvent close(Player player) {
        Validate.notNull(player);
        return new UpdateGuildKungFuFormEvent(player, Command.CLOSE, "");
    }

    public static UpdateGuildKungFuFormEvent text(Player player, String text) {
        Validate.notNull(player);
        Validate.notNull(text);
        return new UpdateGuildKungFuFormEvent(player, Command.TEXT, text);
    }
}

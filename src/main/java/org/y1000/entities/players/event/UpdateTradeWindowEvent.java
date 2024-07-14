package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.ValueEnum;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateTradeWindowPacket;

public final class UpdateTradeWindowEvent extends AbstractPlayerEvent {
    public enum Type implements ValueEnum  {
        CLOSE(1),
        UPDATE(2),
        ;

        private final int v;

        Type(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }

        public static Type fromValue(int v) {
            return ValueEnum.fromValueOrThrow(values(), v);
        }
    }

    private final Type type;

    private final String name;

    private final long number;

    private final int slot;

    public UpdateTradeWindowEvent(Player player, Type type, String name, long number, int slot) {
        super(player);
        this.type = type;
        this.name = name;
        this.number = number;
        this.slot = slot;
    }


    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        UpdateTradeWindowPacket.Builder builder = UpdateTradeWindowPacket.newBuilder().setType(type.value());
        if (name != null) {
            builder.setName(name).setNumber(number);
        }
        return Packet.newBuilder()
                .setUpdateTradeWindow(builder)
                .build();
    }

    public static UpdateTradeWindowEvent close(Player player) {
        return new UpdateTradeWindowEvent(player, Type.CLOSE, null, 0, 0);
    }
}

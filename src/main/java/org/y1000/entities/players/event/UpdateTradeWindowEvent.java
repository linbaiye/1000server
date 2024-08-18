package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.message.ValueEnum;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateTradeWindowPacket;

public final class UpdateTradeWindowEvent extends AbstractPlayerEvent {
    public enum Type implements ValueEnum  {
        CLOSE_WINDOW(1),
        ADD_ITEM(2),
        REMOVE_ITEM(3),
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

    private final boolean self;
    private final int color;

    private UpdateTradeWindowEvent(Player player, Type type, String name, long number, int slot, boolean self, int color) {
        super(player, true);
        this.type = type;
        this.name = name;
        this.number = number;
        this.slot = slot;
        this.self = self;
        this.color = color;
    }


    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        UpdateTradeWindowPacket.Builder builder = UpdateTradeWindowPacket.newBuilder()
                .setType(type.value());
        if (type == Type.ADD_ITEM) {
            builder.setName(name)
                    .setNumber(number)
                    .setSelf(self)
                    .setColor(color)
                    .setSlot(slot);
        } else if (type == Type.REMOVE_ITEM) {
            builder.setSlot(slot).setSelf(self);
        }
        return Packet.newBuilder()
                .setUpdateTradeWindow(builder)
                .build();
    }

    public static UpdateTradeWindowEvent close(Player player) {
        return new UpdateTradeWindowEvent(player, Type.CLOSE_WINDOW, null, 0, 0, true, 0);
    }

    public static UpdateTradeWindowEvent add(Player player, int slot, Item item, boolean myWindow) {
        return new UpdateTradeWindowEvent(player, Type.ADD_ITEM, item.name(),
                (item instanceof StackItem stackItem) ? stackItem.number() : 1, slot, myWindow, item.color());
    }

    public static UpdateTradeWindowEvent remove(Player player, int slot, boolean myWindow) {
        return new UpdateTradeWindowEvent(player, Type.REMOVE_ITEM, null, 0, slot, myWindow, 0);
    }
}

package org.y1000.message.clientevent;

import org.y1000.message.ValueEnum;
import org.y1000.network.gen.ClientUpdateTradePacket;

public record ClientUpdateTradeEvent(int slot, long number, ClientUpdateType type, int tradeWindowSlot) implements ClientEvent {

    public enum ClientUpdateType implements ValueEnum {
        ADD_ITEM(1),
        REMOVE_ITEM(2),
        CONFIRM(3),
        CANCEL(4),
        ;
        private final int v;

        ClientUpdateType(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }
        public static ClientUpdateType fromValue(int v) {
            return ValueEnum.fromValueOrThrow(values(), v);
        }
    }


    public static ClientUpdateTradeEvent fromPacket(ClientUpdateTradePacket packet) {
        return new ClientUpdateTradeEvent(packet.getInventorySlot(), packet.getItemNumber(), ClientUpdateType.fromValue(packet.getType()), packet.getTradeWindowSlot());
    }
}

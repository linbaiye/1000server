package org.y1000.message.clientevent;

import org.y1000.network.gen.ClientSellItemsPacket;
import org.y1000.trade.TradeItem;

import java.util.List;

public record ClientSellEvent(long merchantId, List<TradeItem> items) implements ClientEvent {

    @Override
    public String toString() {
        return "ClientSellEvent{" +
                "merchantId=" + merchantId +
                ", items=" + items +
                '}';
    }

    public static ClientSellEvent fromPacket(ClientSellItemsPacket packet) {
        List<TradeItem> items = packet.getItemsList().stream()
                .map(i -> new TradeItem(i.getName(), (int)i.getNumber(), i.getSlotId()))
                .toList();
        return new ClientSellEvent(packet.getMerchantId(), items);
    }
}

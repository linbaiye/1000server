package org.y1000.message.clientevent;

import org.y1000.network.gen.ClientMerchantTradeItemsPacket;
import org.y1000.trade.TradeItem;

import java.util.List;

public record ClientBuyItemsEvent(long merchantId, List<TradeItem> items) implements ClientEvent {

    public static ClientBuyItemsEvent fromPacket(ClientMerchantTradeItemsPacket packet) {
        List<TradeItem> items = packet.getItemsList().stream()
                .map(i -> new TradeItem(i.getName(), (int)i.getNumber(), i.getSlotId()))
                .toList();
        return new ClientBuyItemsEvent(packet.getMerchantId(), items);
    }
}

package org.y1000.message.clientevent;

import com.google.protobuf.ProtocolStringList;
import org.y1000.network.gen.ClientSellItemsPacket;

import java.util.ArrayList;
import java.util.List;

public record ClientSellEvent(long merchantId, List<Item> items) implements ClientEvent {

    public record Item(String name, int number) {

    }

    @Override
    public String toString() {
        return "ClientSellEvent{" +
                "merchantId=" + merchantId +
                ", items=" + items +
                '}';
    }

    public static ClientSellEvent fromPacket(ClientSellItemsPacket items) {
        List<Integer> itemNumbersList = items.getItemNumbersList();
        ProtocolStringList itemNamesList = items.getItemNamesList();
        if (itemNumbersList.size() != itemNamesList.size() || itemNamesList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<Item> itemList = new ArrayList<>(itemNumbersList.size());
        for (int i = 0; i < itemNumbersList.size(); i++) {
            itemList.add(new Item(itemNamesList.get(i), itemNumbersList.get(i)));
        }
        return new ClientSellEvent(items.getMerchantId(), itemList);
    }
}

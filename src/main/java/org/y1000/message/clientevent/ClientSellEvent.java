package org.y1000.message.clientevent;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.creatures.npc.interactability.BuyInteractability;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.network.gen.ClientMerchantTradeItemsPacket;
import org.y1000.trade.TradeItem;

import java.util.List;

public class ClientSellEvent extends AbstractClientEvent implements ClientSingleInteractEvent {

    private final long merchantId;

    private final List<TradeItem> items;

    private final ItemFactory itemFactory;

    public ClientSellEvent(long merchantId, List<TradeItem> items, ItemFactory itemFactory) {
        this.merchantId = merchantId;
        this.items = items;
        this.itemFactory = itemFactory;
    }

    @Override
    public String toString() {
        return "ClientSellEvent{" +
                "merchantId=" + merchantId +
                ", items=" + items +
                '}';
    }

    public static ClientSellEvent fromPacket(ClientMerchantTradeItemsPacket packet, ItemFactory itemFactory) {
        List<TradeItem> items = packet.getItemsList().stream()
                .map(i -> new TradeItem(i.getName(), (int)i.getNumber(), i.getSlotId()))
                .toList();
        return new ClientSellEvent(packet.getMerchantId(), items, itemFactory);
    }

    @Override
    public long targetId() {
        return merchantId;
    }

    @Override
    public void handle(Player player, ActiveEntity entity) {
        if (!(entity instanceof InteractableNpc npc) || player == null)
            return;
        npc.findFirstInteractability(BuyInteractability.class)
                .ifPresent(buyInteractability -> buyInteractability.buy(player, items, itemFactory::createMoney));
    }
}

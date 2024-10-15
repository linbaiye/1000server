package org.y1000.entities.creatures.npc.interactability;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.creatures.npc.MerchantItem;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.message.serverevent.InteractionMenuEvent;
import org.y1000.trade.TradeItem;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public final class SellInteractability extends AbstractMerchantInteractability {

    private final List<MerchantItem> sellItems;

    public SellInteractability(List<MerchantItem> sellItems) {
        Validate.isTrue(sellItems != null && !sellItems.isEmpty());
        this.sellItems = sellItems;
    }

    @Override
    public String playerSeeingName() {
        return "买物品";
    }

    @Override
    public void interact(Player clicker, InteractableNpc npc) {
        if (clicker == null || npc == null)
            return;
        clicker.emitEvent(InteractionMenuEvent.sellingMenu(clicker, npc, "请问需要什么？", sellItems));
    }

    public void sell(Player player,
                     Collection<TradeItem> playerBuyingItems,
                     BiFunction<String, Long, Item> itemCreator) {
        if (player == null || playerBuyingItems == null || itemCreator == null) {
            return;
        }
        var cost = computeTotal(playerBuyingItems, sellItems);
        if (player.inventory().canBuy(playerBuyingItems, cost) && containsAll(playerBuyingItems, sellItems)) {
            player.inventory().buy(playerBuyingItems, cost, player, itemCreator);
        }
    }
}

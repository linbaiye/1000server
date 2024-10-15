package org.y1000.entities.creatures.npc.interactability;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.creatures.npc.MerchantItem;
import org.y1000.entities.players.Player;
import org.y1000.item.StackItem;
import org.y1000.message.serverevent.InteractionMenuEvent;
import org.y1000.trade.TradeItem;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public final class BuyInteractability extends AbstractMerchantInteractability {
    private final List<MerchantItem> buyItems;

    public BuyInteractability(List<MerchantItem> buyItems) {
        Validate.isTrue(buyItems != null && !buyItems.isEmpty());
        this.buyItems = buyItems;
    }

    public void buy(Player player,
                    Collection<TradeItem> playerSellingItems,
                    Function<Long, StackItem> moneyCreator) {
        if (player == null || playerSellingItems == null || moneyCreator == null) {
            return;
        }
        if (player.inventory().canSell(playerSellingItems) && containsAll(playerSellingItems, buyItems)) {
            player.inventory().sell(playerSellingItems, moneyCreator.apply(computeTotal(playerSellingItems, buyItems)), player);
        }
    }

    @Override
    public String playerSeeingName() {
        return "卖物品";
    }

    @Override
    public void interact(Player clicker, InteractableNpc npc) {
        if (clicker == null || npc == null)
            return;
        clicker.emitEvent(InteractionMenuEvent.buyingMenu(clicker, npc, "需要卖什么吗？", buyItems));
    }
}

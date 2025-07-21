package org.y1000.entities.creatures.npc;


import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.entities.players.event.UpdateInventorySlotMessage;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;

import java.util.*;

public class NpcSellTrade {

    private final Npc npc;

    private final Player player;

    private final List<NpcTradeItem> playerBuyItems;

    private final ItemFactory itemFactory;

    private final int moneySlot;

    public NpcSellTrade(Npc npc, Player player, ItemFactory itemFactory) {
        this.npc = npc;
        this.player = player;
        this.itemFactory = itemFactory;
        this.playerBuyItems = new ArrayList<>();
        moneySlot = player.inventory().findFirstSlot("钱币");
    }

    private void buyItem(MerchantItem item, int number) {
        if (player.inventory().isFull()) {
            player.sendEvent(PlayerTextMessage.of(player, "物品栏已满。"));
            return;
        }
        var i = itemFactory.createItem(item.name());
        int slot = player.inventory().add(i);
        NpcTradeItem e = new NpcTradeItem(item, slot);
        e.addNumber(number);
        playerBuyItems.add(e);
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
    }

    public void onPlayerBuyItem(MerchantItem item, int number) {
        int cost = number * item.price();
        if (moneySlot == 0 || !player.inventory().hasEnough(moneySlot, cost)) {
            player.sendEvent(PlayerTextMessage.of(player, "钱币不足。"));
            return;
        }
        if (!item.canStack()) {
            if (number != 1)
                return;
            buyItem(item, number);
            return;
        }
        for (NpcTradeItem buyItem: playerBuyItems) {
            if (buyItem.nameEquals(item.name())) {
                buyItem.addNumber(number);
                player.sendEvent(UpdateInventorySlotMessage.update(player, buyItem.getInventorySlot()));
                return;
            }
        }
        buyItem(item, number);
    }

    public boolean canKeep() {
        return player.canBeAttacked() && npc.findAbility(HurtAbility.class).map(hurtAbility -> hurtAbility.currentLife() > 0).orElse(true)
                && player.canBeSeenAt(npc.coordinate());
    }

    public boolean isTrading(Player player, Npc npc) {
        return this.player.equals(player) && this.npc.equals(npc);
    }
}

package org.y1000.entities.creatures.npc;


import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Player;

import java.util.ArrayList;
import java.util.List;

public class NpcSellTrade {

    private final Npc npc;

    private final Player player;

    private final List<NpcTradeItem> playerBuyItems;

    private int cost;


    public NpcSellTrade(Npc npc, Player player) {
        this.npc = npc;
        this.player = player;
        this.playerBuyItems = new ArrayList<>();
    }

    public void onPlayerBuyItem(Player player, Npc npc, MerchantItem item, int number) {
        if (item.canStack()) {

        }
        for (NpcTradeItem buyItem: playerBuyItems) {
            if (buyItem.nameEquals(item.name()) && item.canStack()) {
                buyItem.addNumber(number);
            } else {
            }
        }

    }

    public boolean canKeep() {
        return player.canBeAttacked() && npc.findAbility(HurtAbility.class).map(hurtAbility -> hurtAbility.currentLife() > 0).orElse(true)
                && player.canBeSeenAt(npc.coordinate());
    }

    public boolean isTrading(Player player, Npc npc) {
        return this.player.equals(player) && this.npc.equals(npc);
    }
}

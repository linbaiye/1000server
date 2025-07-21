package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.NpcSellMenuMessage;
import org.y1000.entities.players.event.UpdateInventoryMessage;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.sdb.NpcSettingSdb;

import java.util.*;

@Getter
public class NpcSellAbility implements NpcNamedAbility {

    private final long id;
    private final String name;
    private final String sprite;
    private final List<MerchantItem> items;
    private final int image;
    private final String greetings;

    public static final String NAME = "购买物品";

    private final List<NpcSellTrade> trades;

    private final ItemFactory itemFactory;

    private NpcSellAbility(long id, String name, String sprite,
                           List<MerchantItem> items,
                           int image, String greetings,
                           ItemFactory itemFactory) {
        this.id = id;
        this.name = name;
        this.sprite = sprite;
        this.items = items;
        this.image = image;
        this.greetings = greetings;
        this.itemFactory = itemFactory;
        trades = new ArrayList<>();
    }

    public static NpcSellAbility build(long id, NpcSettingSdb sdb, ItemSdb itemSdb, String sprite) {
        Validate.isTrue(!sdb.getSellItems().isEmpty());
        List<MerchantItem> list = sdb.getSellItems().stream()
                .map(n -> new MerchantItem(n, itemSdb.getPrice(n), itemSdb.getIcon(n), itemSdb.getColor(n), itemSdb.canStack(n)))
                .toList();
        return new NpcSellAbility(id, sdb.getSellTitle(), sprite, list, sdb.getSellImage(), sdb.getSellCaption(), null);
    }

    @Override
    public String name() {
        return NAME;
    }

    public void onPlayerBuy(Player player, Npc npc, String name, int number) {
        MerchantItem item = items.stream().filter(i -> i.name().equals(name)).findFirst().orElse(null);
        if (item == null)
            return;
        for (NpcSellTrade trade : trades) {
            if (trade.isTrading(player, npc)) {
                trade.onPlayerBuyItem(player, npc, item, number);
                return;
            }
        }
        NpcSellTrade t = new NpcSellTrade(npc, player);
        trades.add(t);
        t.onPlayerBuyItem(player, npc, item, number);
    }

    @Override
    public void startInteract(Player player) {
        player.sendEvent(UpdateInventoryMessage.quiet(player));
        player.sendEvent(NpcSellMenuMessage.of(player, this));
    }

    public void cooldown(int delta) {
    }
}

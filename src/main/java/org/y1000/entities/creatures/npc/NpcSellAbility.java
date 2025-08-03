package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.*;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.sdb.NpcSettingSdb;

import java.util.*;

public class NpcSellAbility extends AbstractNpcTradeAbility {


    public static final String NAME = "购买物品";

    private NpcSellAbility(long id, String name, String sprite,
                           List<MerchantItem> items,
                           int image, String greetings,
                           ItemFactory itemFactory) {
        super(id, name, sprite, items, image, greetings, itemFactory);
    }

    public static NpcSellAbility build(long id, NpcSettingSdb sdb, ItemSdb itemSdb, String sprite, ItemFactory itemFactory) {
        Validate.isTrue(!sdb.getSellItems().isEmpty());
        List<MerchantItem> list = sdb.getSellItems().stream()
                .map(n -> new MerchantItem(n, itemSdb.getPrice(n), itemSdb.getIcon(n), itemSdb.getColor(n), itemSdb.canStack(n)))
                .toList();
        return new NpcSellAbility(id, sdb.getSellTitle(), sprite, list, sdb.getSellImage(), sdb.getSellCaption(), itemFactory);
    }

    @Override
    public String name() {
        return NAME;
    }

    public void onPlayerBuy(Player player, Npc npc, String name, int number) {
        if (stateOrDistanceInvalid(player, npc) || number < 1)
            return;
        MerchantItem item = getItems().stream().filter(i -> i.name().equals(name)).findFirst().orElse(null);
        if (item == null || (!item.canStack() && number > 1))
            return;
        if (!player.inventory().hasEnough("钱币", number * item.price())) {
            player.sendEvent(PlayerTextMessage.bottom(player, "持有钱币不足。"));
            return;
        }
        Item newItem = getItemFactory().createItem(name, number);
        if (!player.inventory().canPick(newItem)) {
            player.sendEvent(PlayerTextMessage.bottom(player, "物品栏已满。"));
            return;
        }
        int slot = player.inventory().add(newItem);
        int moneySlot = player.inventory().findFirstSlot("钱币");
        player.inventory().decrease(moneySlot, (long) number * item.price());
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
        player.sendEvent(UpdateInventorySlotMessage.update(player, moneySlot));
        newItem.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
    }

    @Override
    public void startInteract(Player player, Npc npc) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        player.sendEvent(UpdateInventoryMessage.quiet(player));
        player.sendEvent(NpcTradeMenuMessage.sale(player, this));
    }
}

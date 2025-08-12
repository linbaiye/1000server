package org.y1000.entities.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.*;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.item.StackItem;
import org.y1000.sdb.NpcSettingSdb;

import java.util.List;


public class NpcBuyAbility extends AbstractNpcTradeAbility {
    private static final String NAME = "出售物品";

    private NpcBuyAbility(long id, String name, String sprite, List<MerchantItem> items, int image, String greetings, ItemFactory itemFactory) {
        super(id, name, sprite, items, image, greetings, itemFactory);
    }


    @Override
    public void decorateMenuActions(List<String> menuActions) {
        menuActions.add(NAME);
    }

    @Override
    public boolean supportsAction(String name) {
        return NAME.equals(name);
    }

    public void onPlayerBuy(Player player, Npc npc, int slot, int number) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Item item = player.inventory().getItem(slot);
        if (item == null)
            return;
        MerchantItem merchantItem = getItems().stream().filter(i -> i.name().equals(item.name())).findFirst().orElse(null);
        if (merchantItem == null)
            return;
        if (item instanceof StackItem stackItem) {
            if (stackItem.number() < 1)
                return;
        } else if  (number != 1) {
            return;
        }
        int moneySlot = player.inventory().findFirstSlot("钱币");
        if (moneySlot == 0 && player.inventory().isFull()) {
            player.sendEvent(PlayerTextMessage.bottom(player, "物品栏已满。"));
            return;
        }
        player.inventory().decrease(slot, number);
        int gainMoney = merchantItem.price() * number;
        if (moneySlot == 0) {
            moneySlot = player.inventory().add(getItemFactory().createItem("钱币", gainMoney));
        } else {
            player.inventory().increase(moneySlot, gainMoney);
        }
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
        player.sendEvent(UpdateInventorySlotMessage.update(player, moneySlot));
        player.inventory().getItem(moneySlot).eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
    }

    @Override
    public void onAbilityClicked(Player player, Npc npc, String abilityName) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        player.sendEvent(UpdateInventoryMessage.quiet(player));
        player.sendEvent(NpcTradeMenuMessage.buy(player, this));
    }

    public static NpcBuyAbility build(long id, NpcSettingSdb sdb, ItemSdb itemSdb, String sprite, ItemFactory itemFactory) {
        Validate.isTrue(!sdb.getBuyItems().isEmpty());
        List<MerchantItem> list = sdb.getBuyItems().stream()
                .map(n -> new MerchantItem(n, itemSdb.getPrice(n), itemSdb.getIcon(n), itemSdb.getColor(n), itemSdb.canStack(n)))
                .toList();
        return new NpcBuyAbility(id, sdb.getTitle(), sprite, list, sdb.getImage(), sdb.getBuyCaption(), itemFactory);
    }
}

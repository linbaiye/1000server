package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemSdb;
import org.y1000.sdb.NpcSettingSdb;

import java.util.List;

@Getter
public class NpcSellAbility implements NpcNamedAbility {

    private final long id;
    private final String name;
    private final String sprite;
    private final List<MerchantItem> items;
    private final int image;
    private final String greetings;

    public static final String NAME = "购买物品";

    private NpcSellAbility(long id, String name, String sprite, List<MerchantItem> items, int image, String greetings) {
        this.id = id;
        this.name = name;
        this.sprite = sprite;
        this.items = items;
        this.image = image;
        this.greetings = greetings;
    }

    public static NpcSellAbility build(long id, NpcSettingSdb sdb, ItemSdb itemSdb, String sprite) {
        Validate.isTrue(!sdb.getSellItems().isEmpty());
        List<MerchantItem> list = sdb.getSellItems().stream()
                .map(n -> new MerchantItem(n, itemSdb.getPrice(n), itemSdb.getIcon(n), itemSdb.getColor(n), itemSdb.canStack(n)))
                .toList();
        return new NpcSellAbility(id, sdb.getSellTitle(), sprite, list, sdb.getSellImage(), sdb.getSellCaption());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void startInteract(Player player) {
        player.sendEvent();
    }
}

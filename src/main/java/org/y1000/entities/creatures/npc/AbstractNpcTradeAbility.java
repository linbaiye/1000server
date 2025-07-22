package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;

import java.util.List;

@Getter
public abstract class AbstractNpcTradeAbility implements NpcNamedAbility {
    private final long id;
    private final String name;
    private final String sprite;
    private final List<MerchantItem> items;
    private final int image;
    private final String greetings;

    private final ItemFactory itemFactory;

    public AbstractNpcTradeAbility(long id, String name,
                                   String sprite,
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
    }

    protected boolean stateOrDistanceInvalid(Player player, Npc npc) {
        return !npc.canBeSeenAt(player.coordinate()) ||
                player.isDead() || player.isLeftGame() ||
                npc.findAbility(HurtAbility.class).map(h -> h.currentLife() <= 0).orElse(true);
    }


}

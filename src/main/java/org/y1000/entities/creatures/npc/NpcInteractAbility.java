package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.NpcMenuMessage;
import org.y1000.sdb.NpcSettingSdb;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NpcInteractAbility {

    private final long id;
    private final String name;

    private static final String greetings = "有什么可以为侠士效劳的吗?";

    private final String sprite;

    private final int image;

    private final List<String> supportedActions;

    private NpcInteractAbility(long id, String name,
                               String sprite,
                               int captionIndex,
                               List<String> supportedActions) {
        this.id = id;
        this.name = name;
        this.sprite = sprite;
        this.image = captionIndex;
        this.supportedActions = supportedActions;
    }

    public String getGreetings() {
        return greetings;
    }

    public void interactedBy(Player player) {
        player.sendEvent(NpcMenuMessage.populate(player, this));
    }

    public static NpcInteractAbility build(NpcSettingSdb sdb, String sprite, long id) {
        List<String> actions = new ArrayList<>();
        if (!sdb.getSellItems().isEmpty())
            actions.add(NpcSellAbility.NAME);
        if (!sdb.getBuyItems().isEmpty())
            actions.add("出售物品");
        return new NpcInteractAbility(id, sdb.getAnyTitle(), sprite, sdb.getAnyImage(), actions);
    }

}

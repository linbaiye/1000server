package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;

import java.util.List;

public class NpcInteractAbility {

    private final String name;

    private static final String greetings = "有什么可以为侠士效劳的吗?";

    private final String sprite;

    private final int captionIndex;

    private final List<String> supportedActions;

    public NpcInteractAbility(String name,
                                     String sprite,
                                     int captionIndex,
                                     List<String> supportedActions) {
        this.name = name;
        this.sprite = sprite;
        this.captionIndex = captionIndex;
        this.supportedActions = supportedActions;
    }

    public void interactedBy(Player player) {
    }

}

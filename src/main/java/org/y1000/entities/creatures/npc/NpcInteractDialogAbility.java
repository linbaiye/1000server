package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.NpcMenuMessage;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.sdb.NpcSettingSdb;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NpcInteractDialogAbility {

    private final long id;

    private final String name;

    private static final String greetings = "有什么可以为侠士效劳的吗?";

    private final String sprite;

    private final int image;

    private NpcInteractDialogAbility(long id, String name,
                                     String sprite,
                                     int captionIndex) {
        this.id = id;
        this.name = name;
        this.sprite = sprite;
        this.image = captionIndex;
    }

    public String getGreetings() {
        return greetings;
    }

    public void interact(Player player, ActiveEntity entity) {
        if (player.isDead() || player.isLeftRealm())
            return;
        List<String> actions = new ArrayList<>();
        entity.findAbility(NpcBuyAbility.class).ifPresent(a -> a.decorateMenuActions(actions));
        entity.findAbility(NpcSellAbility.class).ifPresent(a -> a.decorateMenuActions(actions));
        if (!actions.isEmpty())
            player.sendEvent(NpcMenuMessage.populate(player, this, actions));
        else
            entity.clickText().ifPresent(text -> player.sendEvent(PlayerTextMessage.bottom(player,text)));
    }

    public static NpcInteractDialogAbility build(NpcSettingSdb sdb, String sprite, long id) {
        return new NpcInteractDialogAbility(id, sdb.getAnyTitle(), sprite, sdb.getAnyImage());
    }
}

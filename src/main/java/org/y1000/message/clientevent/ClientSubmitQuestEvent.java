package org.y1000.message.clientevent;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.Quester;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.message.serverevent.EntityChatEvent;
import org.y1000.quest.Quest;

@Getter
public class ClientSubmitQuestEvent extends AbstractClientEvent implements ClientSingleInteractEvent {

    public final String questName;

    public final long npcId;

    private final ItemFactory itemFactory;

    public ClientSubmitQuestEvent(long npcId,
                                  String questName,
                                  ItemFactory itemFactory) {
        Validate.notNull(questName);
        Validate.notNull(itemFactory);
        this.questName = questName;
        this.npcId = npcId;
        this.itemFactory = itemFactory;
    }

    @Override
    public long targetId() {
        return npcId;
    }

    @Override
    public void handle(Player player, ActiveEntity entity) {
        if (player == null || !(entity instanceof Quester quester) || quester.id() != npcId) {
            return;
        }
        Quest quest = quester.getQuest();
        if (!quest.getQuestName().equals(questName)) {
            return;
        }
        var check = quest.canComplete(player);
        if (check != null) {
            entity.emitEvent(new EntityChatEvent(entity, quester.viewName() + "：" +check));
            return;
        }
        quest.complete(player, itemFactory::createItem);
    }
}

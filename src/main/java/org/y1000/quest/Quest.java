package org.y1000.quest;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.sdb.QuestSdb;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public final class Quest {

    public record QuestItem(String itemName, int number) {
    }

    @Getter
    private final String questName;
    @Getter
    private final String submitText;
    private final List<QuestItem> requiredItem;
    private final QuestItem rewardItem;

    @Getter
    private final String description;

    public Quest(String questName, String submitText, List<QuestItem> requiredItem, QuestItem rewardItem,
                 String description) {
        Validate.notNull(questName);
        Validate.notNull(submitText);
        Validate.notNull(requiredItem);
        Validate.notNull(rewardItem);
        Validate.notNull(description);
        this.description = description;
        this.questName = questName;
        this.submitText = submitText;
        this.requiredItem = requiredItem;
        this.rewardItem = rewardItem;
    }

    public String canComplete(Player player) {
        Validate.notNull(player);
        for (QuestItem item : requiredItem) {
            if (!player.inventory().hasEnough(item.itemName(), item.number())) {
                return "需要" + item.number() + "个" + item.itemName() + "。";
            }
        }
        if (player.inventory().isFull()) {
            return "物品栏已满。";
        }
        return null;
    }


    public void complete(Player player, BiFunction<String, Integer, Item> itemCreator) {
        if (canComplete(player) != null) {
            return;
        }
        for (QuestItem item : requiredItem) {
            int slot = player.inventory().consume(item.itemName(), item.number());
            player.emitEvent(new UpdateInventorySlotEvent(player, slot));
        }
        Item item = itemCreator.apply(rewardItem.itemName(), rewardItem.number());
        int slot = player.inventory().put(item);
        player.emitEvent(new UpdateInventorySlotEvent(player, slot));
        item.eventSound().ifPresent(s -> player.emitEvent(new EntitySoundEvent(player, s)));
        player.emitEvent(PlayerTextEvent.pickedItem(player, item.name(), rewardItem.number()));
    }


    public static Quest parse(String questName, QuestSdb questSdb) {
        String requiredItems = questSdb.getRequiredItems(questName);
        String reward = questSdb.getReward(questName);
        List<QuestItem> rewardItems = parseString(reward);
        Validate.isTrue(rewardItems.size() == 1, "Multiple rewards not supported.");
        return new Quest(questName, questSdb.getSubmitText(questName), parseString(requiredItems), rewardItems.get(0), questSdb.getDescription(questName));
    }

    private static List<QuestItem> parseString(String requiredItems) {
        String[] itemAndNumber = requiredItems.split("\\|");
        List<QuestItem> requiredItemList = new ArrayList<>();
        for (String s : itemAndNumber) {
            String[] strings = s.split(":");
            var name = strings[0];
            var number = Integer.parseInt(strings[1]);
            requiredItemList.add(new QuestItem(name, number));
        }
        return requiredItemList;
    }
}

package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.creatures.npc.event.NpcSayEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.NpcQuestMessage;
import org.y1000.entities.players.event.PlayerSoundEvent;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.entities.players.event.UpdateInventoryMessage;
import org.y1000.item.Item;
import org.y1000.item.ItemFactory;
import org.y1000.sdb.QuestSdb;

import java.util.*;

public class NpcQuestAbility implements NpcInteractAbility {

    @Getter
    private static class Quest {
        private final String name;
        private final Map<String, Integer> requiredItems;

        private final List<Item> rewardItems;
        private final String abstraction;
        private final String description;
        private final String submit;
        public Quest(String name,
                     Map<String, Integer> requiredItems,
                     List<Item> rewardItems,
                     String abstraction,
                     String description,
                     String submit) {
            this.name = name;
            this.requiredItems = requiredItems;
            this.rewardItems = rewardItems;
            this.abstraction = abstraction;
            this.description = description;
            this.submit = submit;
        }
    }

    private final List<Quest> quests;

    private NpcQuestAbility(List<Quest> quests) {
        this.quests = quests;
    }


    @Override
    public void decorateMenuActions(List<String> menuActions) {
        quests.forEach(q -> menuActions.add(q.getName()));
    }

    @Override
    public boolean supportsAction(String name) {
        return quests.stream().anyMatch(q -> q.getName().equals(name));
    }

    private void submitQuest(Npc npc, Quest quest, Player player) {
        for (Map.Entry<String, Integer> nameNumber: quest.requiredItems.entrySet()) {
            if (!player.inventory().hasEnough(nameNumber.getKey(), nameNumber.getValue())) {
                npc.sendEvent(NpcSayEvent.say(npc, "阿弥陀佛，施主何故戏耍老衲。"));
                return;
            }
        }
        for (Item rewardItem : quest.getRewardItems()) {
            if (!player.inventory().canAdd(rewardItem)) {
                player.sendEvent(PlayerTextMessage.bottom(player, "物品栏已满。"));
                return;
            }
        }
        for (Map.Entry<String, Integer> nameNumber: quest.requiredItems.entrySet()) {
            player.inventory().decrease(nameNumber.getKey(), nameNumber.getValue());
        }
        for (Item rewardItem : quest.getRewardItems()) {
            player.inventory().add(rewardItem);
            player.sendEvent(PlayerTextMessage.gainItem(player, rewardItem));
            rewardItem.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
        }
        player.sendEvent(UpdateInventoryMessage.quiet(player));
    }

    public void submit(Player player, Npc npc, String questName) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        for (Quest quest : quests) {
            if (quest.name.equals(questName)) {
                submitQuest(npc, quest, player);
                return;
            }
        }
    }

    @Override
    public void onAbilityClicked(Player player, Npc npc, String abilityName) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        for (Quest quest : quests) {
            if (quest.name.equals(abilityName)) {
                player.sendEvent(NpcQuestMessage.toPlayer(player, npc.id(), npc.viewName(), quest.name, quest.getAbstraction(), quest.getDescription(), quest.submit));
                return;
            }
        }
    }

    private static Map<String, Integer> parseItems(String requiredItems) {
        String[] itemAndNumber = requiredItems.split("\\|");
        Map<String, Integer> ret = new HashMap<>();
        for (String s : itemAndNumber) {
            String[] strings = s.split(":");
            var name = strings[0];
            var number = Integer.parseInt(strings[1]);
            ret.put(name, number);
        }
        return ret;
    }


    private static Quest buildQuest(String questId, QuestSdb questSdb, ItemFactory itemFactory) {
        String menuName = questSdb.getMenuName(questId);
        Map<String, Integer> required = parseItems(questSdb.getRequiredItems(questId));
        Map<String, Integer> reward = parseItems(questSdb.getReward(questId));
        List<Item> items = new ArrayList<>();
        reward.forEach((k, v)-> items.add(itemFactory.createItem(k, v)));
        return new Quest(menuName, required, items, questSdb.getAbstraction(questId),
                questSdb.getDescription(questId), questSdb.getSubmitText(questId));
    }

    public static Optional<NpcQuestAbility> of(String npcName, QuestSdb questSdb, ItemFactory itemFactory) {
        List<String> npcQuestIds = questSdb.getNpcQuestIds(npcName);
        if (npcQuestIds.isEmpty())
            return Optional.empty();
        var quests = npcQuestIds.stream().map(id -> buildQuest(id, questSdb, itemFactory)).toList();
        return Optional.of(new NpcQuestAbility(quests));
    }
}

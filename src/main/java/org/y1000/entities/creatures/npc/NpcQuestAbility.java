package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;
import org.y1000.sdb.QuestSdb;

import java.util.*;

public class NpcQuestAbility implements NpcInteractAbility {

    private record Quest(String name, Map<String, Integer> requiredItems, Map<String, Integer> rewardItems) {

    }

    private final List<Quest> quests;

    private NpcQuestAbility(List<Quest> quests) {
        this.quests = quests;
    }


    @Override
    public void decorateMenuActions(List<String> menuActions) {
        quests.forEach(q -> menuActions.add(q.name()));
    }

    @Override
    public boolean supportsAction(String name) {
        return quests.stream().anyMatch(q -> q.name.equals(name));
    }

    @Override
    public void interact(Player player, Npc npc) {
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


    private static Quest buildQuest(String questId, QuestSdb questSdb) {
        String menuName = questSdb.getMenuName(questId);
        Map<String, Integer> required = parseItems(questSdb.getRequiredItems(questId));
        Map<String, Integer> reward = parseItems(questSdb.getReward(questId));
    }

    public static Optional<NpcQuestAbility> of(String npcName, QuestSdb questSdb) {
        List<String> npcQuestIds = questSdb.getNpcQuestIds(npcName);
        if (npcQuestIds.isEmpty())
            return Optional.empty();
        npcQuestIds.stream().map(id -> )
    }
}

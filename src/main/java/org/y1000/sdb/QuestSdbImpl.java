package org.y1000.sdb;


import java.util.ArrayList;
import java.util.List;

public class QuestSdbImpl extends AbstractCSVSdbReader implements QuestSdb {
    public static final QuestSdbImpl Instance = new QuestSdbImpl();
    private QuestSdbImpl() {
        read("NpcSetting/Quest.sdb", "utf8");
    }

    @Override
    public List<String> getNpcQuestIds(String npcName) {
        List<String> questIds = new ArrayList<>();
        uniqueIds().forEach(id -> {
            if (npcName.equals(get(id, "NpcName"))) {
                questIds.add(id);
            }
        });
        return questIds;
    }

    @Override
    public String getRequiredItems(String questId) {
        return get(questId, "RequiredItems");
    }


    @Override
    public String getReward(String questId) {
        return get(questId, "Reward");
    }

    @Override
    public String getDescription(String questId) {
        return get(questId, "Description");
    }

    @Override
    public String getSubmitText(String questId) {
        return get(questId, "SubmitText");
    }

    @Override
    public String getMenuName(String questId) {
        return get(questId, "MenuName");
    }
}

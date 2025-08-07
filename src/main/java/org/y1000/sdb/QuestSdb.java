package org.y1000.sdb;

import java.util.List;

public interface QuestSdb {
    List<String> getNpcQuestIds(String npcName);

    String getRequiredItems(String questId);

    String getReward(String questId);

    String getDescription(String questId);

    String getSubmitText(String questId);

    String getMenuName(String questId);

}

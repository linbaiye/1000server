package org.y1000.sdb;

import java.util.List;

public interface QuestSdb {
    List<String> getNames();

    String getRequiredItems(String questName);

    String getReward(String questName);

    String getDescription(String questName);

    String getSubmitText(String questName);

    static QuestSdb forNpc(String name) {
        return new QuestSdbImpl(name);
    }

}

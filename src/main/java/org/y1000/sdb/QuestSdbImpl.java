package org.y1000.sdb;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public class QuestSdbImpl extends AbstractSdbReader implements QuestSdb {

    public QuestSdbImpl(String name) {
        Validate.notNull(name);
        read("NpcSetting/" + name + ".sdb", "utf8");
    }

    @Override
    public List<String> getNames() {
        return new ArrayList<>(names());
    }

    @Override
    public String getRequiredItems(String questName) {
        return get(questName, "RequiredItems");
    }

    @Override
    public String getReward(String questName) {
        return get(questName, "Reward");
    }

    @Override
    public String getDescription(String questName) {
        return get(questName, "Description");
    }

    @Override
    public String getSubmitText(String questName) {
        return get(questName, "SubmitText");
    }

    public static QuestSdbImpl forNpc(String name) {
        return new QuestSdbImpl(name);
    }
}

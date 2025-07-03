package org.y1000.sdb;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.creatures.monster.NpcActionEnum;

import java.util.HashMap;
import java.util.Map;

public final class ActionSdb extends AbstractCSVSdbReader {
    public static final ActionSdb INSTANCE = new ActionSdb();
    private ActionSdb() {
        read("Action.sdb");
    }

    private static final Map<NpcActionEnum, String>  ACTION_NAME_MAP = new HashMap<>() {{
        put(NpcActionEnum.Idle, "Idle");
        put(NpcActionEnum.Attack, "Attack");
        put(NpcActionEnum.Move, "Move");
        put(NpcActionEnum.Die, "Die");
        put(NpcActionEnum.Hurt, "Hurt");
        put(NpcActionEnum.Turn, "Freeze");
    }};

    public int getActionLength(String name, NpcActionEnum npcStateEnum) {
        if (!ACTION_NAME_MAP.containsKey(npcStateEnum)) {
            throw new NotImplementedException();
        }
        return getInt(name, ACTION_NAME_MAP.get(npcStateEnum));
    }
}

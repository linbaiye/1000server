package org.y1000.sdb;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.creatures.monster.NpcStateEnum;

import java.util.HashMap;
import java.util.Map;

public final class ActionSdb extends AbstractCSVSdbReader {
    public static final ActionSdb INSTANCE = new ActionSdb();
    private ActionSdb() {
        read("Action.sdb");
    }

    private static final Map<NpcStateEnum, String>  ACTION_NAME_MAP = new HashMap<>() {{
        put(NpcStateEnum.Idle, "Idle");
        put(NpcStateEnum.Attack, "Attack");
        put(NpcStateEnum.Move, "Move");
        put(NpcStateEnum.Die, "Die");
        put(NpcStateEnum.Hurt, "Hurt");
        put(NpcStateEnum.Turn, "Freeze");
    }};

    public int getActionLength(String name, NpcStateEnum npcStateEnum) {
        if (!ACTION_NAME_MAP.containsKey(npcStateEnum)) {
            throw new NotImplementedException();
        }
        return getInt(name, ACTION_NAME_MAP.get(npcStateEnum));
    }
}

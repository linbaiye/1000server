package org.y1000.sdb;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.creatures.npc.NpcAction;

import java.util.HashMap;
import java.util.Map;

public final class ActionSdb extends AbstractCSVSdbReader {
    public static final ActionSdb INSTANCE = new ActionSdb();
    private ActionSdb() {
        read("Action.sdb");
    }

    private static final Map<NpcAction, String>  ACTION_NAME_MAP = new HashMap<>() {{
        put(NpcAction.Idle, "Idle");
        put(NpcAction.Attack, "Attack");
        put(NpcAction.Move, "Move");
        put(NpcAction.Die, "Die");
        put(NpcAction.Hurt, "Hurt");
        put(NpcAction.Turn, "Freeze");
    }};

    public int getActionLength(String animate, NpcAction npcStateEnum) {
        if (!ACTION_NAME_MAP.containsKey(npcStateEnum)) {
            throw new NotImplementedException();
        }
        return getInt(animate, ACTION_NAME_MAP.get(npcStateEnum));
    }
}

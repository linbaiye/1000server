package org.y1000.sdb;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;

import java.util.HashMap;
import java.util.Map;

public final class ActionSdb extends AbstractCSVSdbReader {
    public static final ActionSdb INSTANCE = new ActionSdb();
    private ActionSdb() {
        read("Action.sdb");
    }

    private static final Map<NpcAnimationEnum, String>  ACTION_NAME_MAP = new HashMap<>() {{
        put(NpcAnimationEnum.Idle, "Idle");
        put(NpcAnimationEnum.Attack, "Attack");
        put(NpcAnimationEnum.Move, "Move");
        put(NpcAnimationEnum.Die, "Die");
        put(NpcAnimationEnum.Hurt, "Hurt");
        put(NpcAnimationEnum.Turn, "Freeze");
    }};

    public int getActionLength(String animate, NpcAnimationEnum npcStateEnum) {
        if (!ACTION_NAME_MAP.containsKey(npcStateEnum)) {
            throw new NotImplementedException();
        }
        return getInt(animate, ACTION_NAME_MAP.get(npcStateEnum));
    }
}

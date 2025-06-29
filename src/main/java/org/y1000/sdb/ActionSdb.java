package org.y1000.sdb;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.creatures.OldPlayerStateEnum;

import java.util.HashMap;
import java.util.Map;

public final class ActionSdb extends AbstractCSVSdbReader {
    public static final ActionSdb INSTANCE = new ActionSdb();
    private ActionSdb() {
        read("Action.sdb");
    }

    private static final Map<OldPlayerStateEnum, String>  ACTION_NAME_MAP = new HashMap<>() {{
        put(OldPlayerStateEnum.IDLE, "Idle");
        put(OldPlayerStateEnum.ATTACK, "Attack");
        put(OldPlayerStateEnum.Move, "Move");
        put(OldPlayerStateEnum.DIE, "Die");
        put(OldPlayerStateEnum.HURT, "Hurt");
        put(OldPlayerStateEnum.Turn, "Freeze");
    }};

    public int getActionLength(String name, OldPlayerStateEnum playerStateEnum) {
        if (!ACTION_NAME_MAP.containsKey(playerStateEnum)) {
            throw new NotImplementedException();
        }
        return getInt(name, ACTION_NAME_MAP.get(playerStateEnum));
    }
}

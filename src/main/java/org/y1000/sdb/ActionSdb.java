package org.y1000.sdb;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.creatures.PlayerStateEnum;

import java.util.HashMap;
import java.util.Map;

public final class ActionSdb extends AbstractCSVSdbReader {
    public static final ActionSdb INSTANCE = new ActionSdb();
    private ActionSdb() {
        read("Action.sdb");
    }

    private static final Map<PlayerStateEnum, String>  ACTION_NAME_MAP = new HashMap<>() {{
        put(PlayerStateEnum.IDLE, "Idle");
        put(PlayerStateEnum.ATTACK, "Attack");
        put(PlayerStateEnum.Move, "Move");
        put(PlayerStateEnum.DIE, "Die");
        put(PlayerStateEnum.HURT, "Hurt");
        put(PlayerStateEnum.Turn, "Freeze");
    }};

    public int getActionLength(String name, PlayerStateEnum playerStateEnum) {
        if (!ACTION_NAME_MAP.containsKey(playerStateEnum)) {
            throw new NotImplementedException();
        }
        return getInt(name, ACTION_NAME_MAP.get(playerStateEnum));
    }
}

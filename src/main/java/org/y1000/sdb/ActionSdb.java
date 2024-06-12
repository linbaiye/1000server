package org.y1000.sdb;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.creatures.State;

import java.util.HashMap;
import java.util.Map;

public final class ActionSdb extends AbstractSdbReader {
    public static final ActionSdb INSTANCE = new ActionSdb();
    private ActionSdb() {
        read("Action.sdb");
    }

    private static final Map<State, String>  ACTION_NAME_MAP = new HashMap<>() {{
        put(State.IDLE, "Idle");
        put(State.ATTACK, "Attack");
        put(State.WALK, "Move");
        put(State.DIE, "Die");
        put(State.HURT, "Hurt");
        put(State.FROZEN, "Freeze");
    }};

    public int getActionLength(String name, State state) {
        if (!ACTION_NAME_MAP.containsKey(state)) {
            throw new NotImplementedException();
        }
        return getInt(name, ACTION_NAME_MAP.get(state));
    }
}

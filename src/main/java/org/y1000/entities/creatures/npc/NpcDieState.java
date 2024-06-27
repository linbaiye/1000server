package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractCreatureDieState;

public final class NpcDieState extends AbstractCreatureDieState<Npc> implements NpcState {
    public NpcDieState(int totalMillis) {
        super(totalMillis);
    }

    public static NpcDieState die(int millis) {
        return new NpcDieState(millis + 8000);
    }
}

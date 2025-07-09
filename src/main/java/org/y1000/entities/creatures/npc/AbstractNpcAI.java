package org.y1000.entities.creatures.npc;

import org.y1000.message.NpcSnapshot;

public abstract class AbstractNpcAI implements NpcAI{

    private final Npc npc;

    private NpcAbility currentAbility;

    protected AbstractNpcAI(Npc npc) {
        this.npc = npc;
    }

    <T extends NpcAbility> T changeAbilityOrThrow(Class<T> type) {
        var a = npc.findAbility(type).orElseThrow();
        currentAbility = a;
        return a;
    }

    Npc npc() {
        return npc;
    }

    NpcAbility currentAbility() {
        return currentAbility;
    }

    @Override
    public NpcSnapshot captureSnapshot() {
        return currentAbility.captureSnapshot(npc);
    }
}

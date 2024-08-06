package org.y1000.entities.creatures.npc.spell;

import lombok.Getter;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.NpcCastCloneEvent;
import org.y1000.entities.creatures.npc.Npc;

public final class CloneSpell implements NpcSpell {
    private boolean casted = false;
    private final int lifePercent;

    @Getter
    private final int number;

    public CloneSpell(int lifePercent, int number) {
        this.lifePercent = lifePercent;
        this.number = number;
    }

    @Override
    public boolean canCast(Npc npc) {
        return !casted && npc.stateEnum() != State.DIE &&
                ((float)npc.currentLife() / npc.maxLife()) * 100 <= lifePercent;
    }

    public void castIfAvailable(Npc npc, AttackableActiveEntity entity) {
        if (canCast(npc)) {
            casted = true;
            npc.emitEvent(new NpcCastCloneEvent(npc, number, entity));
        }
    }

    @Override
    public void reset() {
        casted = false;
    }
}

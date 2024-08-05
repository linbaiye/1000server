package org.y1000.entities.creatures.npc.spell;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;

public final class CloneSpell implements NpcSpell {
    private boolean casted = false;
    private final int lifePercent;

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

    @Override
    public void cast(Npc npc) {
        casted = true;
    }

    @Override
    public void reset() {
        casted = false;
    }
}

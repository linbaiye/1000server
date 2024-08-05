package org.y1000.entities.creatures.npc.spell;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.NpcShiftEvent;
import org.y1000.entities.creatures.npc.Npc;

public final class ShiftSpell implements NpcSpell {

    private final String newNpcName;

    public ShiftSpell(String newNpcName) {
        Validate.notNull(newNpcName, "new name can't be null.");
        this.newNpcName = newNpcName;
    }

    @Override
    public boolean canCast(Npc npc) {
        return npc.stateEnum() == State.DIE;
    }

    @Override
    public void cast(Npc npc) {
        npc.emitEvent(new NpcShiftEvent(newNpcName, npc));
    }

}

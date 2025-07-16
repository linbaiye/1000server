package org.y1000.entities.creatures.npc.spell;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.NpcShiftEvent;
import org.y1000.entities.creatures.npc.NpcAction;
import org.y1000.entities.creatures.npc.INpc;

public final class ShiftSpell implements NpcSpell {

    private final String newNpcName;

    public ShiftSpell(String newNpcName) {
        Validate.notNull(newNpcName, "new name can't be null.");
        this.newNpcName = newNpcName;
    }

    @Override
    public boolean canCast(INpc npc) {
        return npc.npcStateEnum() == NpcAction.Die;
    }

    public void cast(INpc npc) {
        if (canCast(npc))
            npc.emitEvent(new NpcShiftEvent(newNpcName, npc));
    }

}

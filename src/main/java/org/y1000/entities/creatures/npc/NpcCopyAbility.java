package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.creatures.npc.event.NpcCopyEvent;

public class NpcCopyAbility {

    private final int percent;

    private final int copyNumber;

    private boolean copied;

    public NpcCopyAbility(int percent, int copyNumber) {
        this.percent = percent;
        this.copyNumber = copyNumber;
        copied = false;
    }

    private boolean isLowerThanPercent(HurtAbility hurtAbility) {
        return (int)(((float)hurtAbility.currentLife() / hurtAbility.maxLife()) * 100) <= percent;
    }

    void tryApply(Npc npc, ActiveEntity enemy) {
        if (copied)
            return;
        if (npc.findAbility(HurtAbility.class).map(this::isLowerThanPercent).orElse(false)) {
            copied = true;
            npc.sendEvent(new NpcCopyEvent(npc, copyNumber, enemy));
        }
    }
}

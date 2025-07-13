package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.event.NpcRemoveEvent;
import org.y1000.message.NpcSnapshot;

public abstract class AbstractNpcAI implements NpcAI {

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

    <A extends NpcAbility> A changeAbility(A ability) {
        currentAbility = ability;
        return ability;
    }


    abstract void onNonDieAbilityDone(NpcAbility ability);

    void updateAbility(int delta) {
        if (!currentAbility().update(delta)) {
            return;
        }
        if (currentAbility() instanceof NpcDieAbility) {
            npc.sendEvent(NpcRemoveEvent.of(npc));
        } else {
            onNonDieAbilityDone(currentAbility());
        }
    }

    Npc npc() {
        return npc;
    }

    NpcAbility currentAbility() {
        return currentAbility;
    }


    void applyHurtAbility(NpcHurtAbility ability) {
        if (currentAbility() instanceof NpcMoveAbility moveAbility) {
            moveAbility.interrupt(npc());
        }
        if (ability.currentLife() <= 0) {
            changeAbilityOrThrow(NpcDieAbility.class)
                    .apply(npc());
        } else {
            ability.apply(npc(), currentAbility());
            changeAbility(ability);
        }
    }

    @Override
    public NpcSnapshot captureSnapshot() {
        return currentAbility.captureSnapshot(npc);
    }
}

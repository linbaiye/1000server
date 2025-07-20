package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.event.NpcRemoveEvent;
import org.y1000.message.NpcSnapshot;

public abstract class AbstractNpcAI implements NpcAI {

    private final Npc npc;

    private NpcUpdatableAbility currentAbility;

    protected AbstractNpcAI(Npc npc) {
        this.npc = npc;
    }

    <T extends NpcUpdatableAbility> T changeAbilityOrThrow(Class<T> type) {
        var a = npc.findAbility(type).orElseThrow();
        currentAbility = a;
        return a;
    }

    <A extends NpcUpdatableAbility> A changeAbility(A ability) {
        currentAbility = ability;
        return ability;
    }


    abstract void onNonDieAbilityDone(NpcUpdatableAbility ability);

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

    NpcUpdatableAbility currentAbility() {
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

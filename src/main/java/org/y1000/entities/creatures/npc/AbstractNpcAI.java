package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
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

    void changeAbility(NpcAbility ability) {
        currentAbility = ability;
    }


    abstract void onAbilityDone(NpcAbility ability);

    void updateAbility(int delta) {
        if (!currentAbility().update(delta)) {
            return;
        }
        if (currentAbility() instanceof NpcHurtAbility hurtAbility) {
            onAbilityDone(hurtAbility.getInterruptedAbility());
        } else {
            onAbilityDone(currentAbility());
        }
    }



    Npc npc() {
        return npc;
    }

    NpcAbility currentAbility() {
        return currentAbility;
    }

    abstract void onAfterHurtStart(ActiveEntity attacker, NpcHurtAbility ability);

    void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        if (currentAbility() instanceof NpcMoveAbility moveAbility) {
            moveAbility.interrupt(npc());
        }
        if (ability.getCurrentLife() <= 0) {
            changeAbilityOrThrow(NpcDieAbility.class)
                    .apply(npc());
            return;
        }
        ability.apply(npc(), currentAbility());
        changeAbility(ability);
        onAfterHurtStart(attacker, ability);
    }

    @Override
    public NpcSnapshot captureSnapshot() {
        return currentAbility.captureSnapshot(npc);
    }
}
